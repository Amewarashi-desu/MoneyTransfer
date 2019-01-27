import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.AbstractMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.IntStream;

public class BlackboxTest {

    public static final String ROOT_URL = "http://localhost:8181/";
    public static final String ACCOUNT_URL = ROOT_URL + "account/";
    public static final String PAYMENT_URL = ROOT_URL + "payment/";

    public static void main(String[] args) {

        final BlackboxTest blackboxTest = new BlackboxTest();

        //Our expected results
        final Map<Long, BigDecimal> accountIdToBalance = new ConcurrentHashMap<>();

        //Helper list for creating random payments
        final List<Long> accountIds = new CopyOnWriteArrayList<>();

        //Creating 1000 accounts
        IntStream.rangeClosed(1, 1000).parallel().forEach(i -> {

            final BigDecimal balance = BigDecimal.valueOf(i * 31 * 100);

            final AbstractMap.SimpleImmutableEntry<Integer, Long> entry = blackboxTest.createAccount(balance);

            if ((201 != entry.getKey())) throw new AssertionError("Expected HTTP status 201 was " + entry.getKey());

            System.out.println("Account ID " + entry.getValue() + " with balance " + balance + " created");
            accountIdToBalance.put(entry.getValue(), balance);
            accountIds.add(entry.getValue());

        });

        final int size = accountIds.size();

        // 10000 random payments
        IntStream.rangeClosed(1, 10000).parallel().forEach(i -> {

            BigDecimal amount = BigDecimal.valueOf(ThreadLocalRandom.current().nextInt(50) + 1);

            final Long leftId = accountIds.get(ThreadLocalRandom.current().nextInt(size));
            final Long rightId = accountIds.get(ThreadLocalRandom.current().nextInt(size));

            //Can't pay myself
            if (leftId.equals(rightId)) {
                return;
            }

            //Payment should not lead to check constraint violation
            if (accountIdToBalance.get(leftId).subtract(amount).compareTo(BigDecimal.ZERO) < 0) {
                return;
            }

            final AbstractMap.SimpleImmutableEntry<Integer, Long> entry = blackboxTest.createPayment(amount, leftId, rightId);

            System.out.println("Payment for " + amount + " from AccountID " + leftId + " to AccountID " + rightId);

            if ((201 != entry.getKey())) throw new AssertionError("Expected HTTP status 201 was " + entry.getKey());

            // update our expected results
            accountIdToBalance.put(leftId, accountIdToBalance.get(leftId).subtract(amount));
            accountIdToBalance.put(rightId, accountIdToBalance.get(rightId).add(amount));

        });

        // actual result vs expected result
        accountIdToBalance
                .entrySet()
                .parallelStream()
                .forEach(entry -> {
                    final BigDecimal actualBalance = blackboxTest.getAccount(entry.getKey()).getValue().getBigDecimal("balance");
                    final BigDecimal expectedBalance = entry.getValue();

                    System.out.println("For account ID " + entry.getKey() + " actual balance is " +
                            actualBalance + " and expected balance is " + expectedBalance);

                    if (actualBalance.compareTo(expectedBalance) != 0)
                        throw new AssertionError("Wrong balance for account ID " + entry.getKey() +
                                "For account ID " + entry.getKey() + " actual balance is " +
                                actualBalance + " and expected balance is " + expectedBalance);
                });

    }

    AbstractMap.SimpleImmutableEntry<Integer, Long> createAccount(BigDecimal initialBalance) {

        try {

            JSONObject json = new JSONObject();
            json.put("initialBalance", initialBalance);

            HttpClient httpClient = HttpClientBuilder.create().build();
            HttpPost request = new HttpPost(ACCOUNT_URL);
            request.addHeader("Content-Type", "application/json");
            StringEntity params = new StringEntity(json.toString());
            request.setEntity(params);

            HttpResponse response = httpClient.execute(request);

            final int statusCode = response.getStatusLine().getStatusCode();
            final String location = response.getFirstHeader("Location").getElements()[0].getName();

            return new AbstractMap.SimpleImmutableEntry<>(statusCode, Long.parseLong(location.substring(location.lastIndexOf("/") + 1)));

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    AbstractMap.SimpleImmutableEntry<Integer, Long> createPayment(BigDecimal amount, Long leftAccountId, Long rightAccountId) {

        try {

            JSONObject json = new JSONObject();
            json.put("leftAccountId", leftAccountId);
            json.put("rightAccountId", rightAccountId);
            json.put("amount", amount);

            HttpClient httpClient = HttpClientBuilder.create().build();
            HttpPost request = new HttpPost(PAYMENT_URL);
            request.addHeader("Content-Type", "application/json");
            StringEntity params = new StringEntity(json.toString());
            request.setEntity(params);

            HttpResponse response = httpClient.execute(request);

            final int statusCode = response.getStatusLine().getStatusCode();
            final String location = response.getFirstHeader("Location").getElements()[0].getName();

            return new AbstractMap.SimpleImmutableEntry<>(statusCode, Long.parseLong(location.substring(location.lastIndexOf("/") + 1)));

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    AbstractMap.SimpleImmutableEntry<Integer, JSONObject> getAccount(Long id) {

        return getIntegerJSONObjectSimpleImmutableEntry(ACCOUNT_URL + id);

    }

    AbstractMap.SimpleImmutableEntry<Integer, JSONObject> getPayment(Long id) {

        return getIntegerJSONObjectSimpleImmutableEntry(PAYMENT_URL + id);

    }

    private AbstractMap.SimpleImmutableEntry<Integer, JSONObject> getIntegerJSONObjectSimpleImmutableEntry(String url) {

        try {

            HttpClient httpClient = HttpClientBuilder.create().build();
            HttpGet request = new HttpGet(url);

            HttpResponse response = httpClient.execute(request);

            HttpEntity entity = response.getEntity();

            String json = EntityUtils.toString(entity, StandardCharsets.UTF_8);
            JSONObject o = new JSONObject(json);

            final int statusCode = response.getStatusLine().getStatusCode();

            return new AbstractMap.SimpleImmutableEntry<>(statusCode, o);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }


}
