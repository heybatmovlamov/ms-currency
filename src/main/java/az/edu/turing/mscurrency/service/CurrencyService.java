package az.edu.turing.mscurrency.service;

import az.edu.turing.mscurrency.dto.CurrencyDto;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.InputStream;
import java.net.URL;

@Service
public class CurrencyService {

    @Autowired
    private StringRedisTemplate redisTemplate;

    private static final String REDIS_KEY = "currency_rates";

    @Scheduled(cron = "0 0 10 * * ?")
    @SneakyThrows
    public void fetchAndUpdateCurrencyData() {
        Document document = configCurrency();

        // Veri çekme
        CurrencyDto usdDto = extractCurrencyData(document, "USD");
        CurrencyDto euroDto = extractCurrencyData(document, "EUR");
        CurrencyDto trlDto = extractCurrencyData(document, "TRY");

        // Redis'e kaydetme
        redisTemplate.opsForHash().put(REDIS_KEY, "USD", usdDto.getValue());
        redisTemplate.opsForHash().put(REDIS_KEY, "EUR", euroDto.getValue());
        redisTemplate.opsForHash().put(REDIS_KEY, "TRY", trlDto.getValue());
    }

    public CurrencyDto getCurrencyFromRedis(String currencyCode) {
        String value = (String) redisTemplate.opsForHash().get(REDIS_KEY, currencyCode);

        if (value == null || value.isEmpty()) {
            Document document = configCurrency();
            CurrencyDto currencyDto = extractCurrencyData(document, currencyCode);
            redisTemplate.opsForHash().put(REDIS_KEY, currencyCode, currencyDto.getValue());
            return currencyDto;
        }

        CurrencyDto currencyDto = new CurrencyDto();
        currencyDto.setValue(value);
        return currencyDto;
    }

    @SneakyThrows
    private Document configCurrency() {
        URL url = new URL("https://www.cbar.az/currencies/19.09.2025.xml");
        InputStream inputStream = url.openStream();

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        return builder.parse(inputStream);
    }

    private CurrencyDto extractCurrencyData(Document document, String currencyCode) {
        NodeList valuteList = document.getElementsByTagName("Valute");
        CurrencyDto currencyDto = new CurrencyDto();
        for (int i = 0; i < valuteList.getLength(); i++) {
            Element valute = (Element) valuteList.item(i);
            String code = valute.getAttribute("Code");
            if (code.equals(currencyCode)) {
                String value = valute.getElementsByTagName("Value").item(0).getTextContent();
                currencyDto.setValue(value);
            }
        }
        return currencyDto;
    }
}
