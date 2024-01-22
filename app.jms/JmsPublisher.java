package com.sample.services;

import com.solacesystems.jms.SupportedProperty;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Hashtable;
import javax.jms.*;
import javax.naming.Context;
import javax.naming.InitialContext;

public class JMSPublisher {
    CredentialService credentialService;

    public JMSPublisher(CredentialService credentialService) {
        this.credentialService = credentialService;
    }

    public void Publish() throws Exception {
        System.out.println("TopicPublisher initializing...");

        // Setting up and Initial Context Lookup Details
        Hashtable<String, Object> env = new Hashtable<>();
        env.put(InitialContext.INITIAL_CONTEXT_FACTORY, "com.solacesystems.jndi.SolJNDIInitialContextFactory");
        env.put(InitialContext.PROVIDER_URL, "smfs://solcfmaudev-primary.macbank:55443");
        env.put(SupportedProperty.SOLACE_JMS_VPN, "sol-cfm-au-dev");
        env.put(Context.SECURITY_PRINCIPAL, "sol_fts_au_dv");
        env.put(Context.SECURITY_CREDENTIALS, credentialService.GetCredentials("SOLACE_SECURITY_CREDENTIALS"));

        // Properties for SSL Session
        env.put(SupportedProperty.SOLACE_JMS_SSL_TRUST_STORE, String.format("%s/solace.truststore.jks", new File("src/main/resources").getPath()));
        env.put(SupportedProperty.SOLACE_JMS_SSL_TRUST_STORE_FORMAT, "JKS");
        env.put(SupportedProperty.SOLACE_JMS_SSL_TRUST_STORE_PASSWORD, credentialService.GetCredentials("SOLACE_JMS_SSL_TRUST_STORE_PASSWORD"));
        env.put(SupportedProperty.SOLACE_JMS_SSL_VALIDATE_CERTIFICATE, true);
        env.put(SupportedProperty.SOLACE_JMS_SSL_VALIDATE_CERTIFICATE_DATE, true);

        // InitialContext is used to look up the JMS administered objects
        InitialContext initialContext = new InitialContext(env);
        // Lookup ConnectionFactory
        ConnectionFactory connectionfactory = (ConnectionFactory)initialContext.lookup("sol_fts_au_dv");
        // JMS Connection
        Connection connection = connectionfactory.createConnection();

        // Create a non-transacted, Auto Ack session.
        final Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);

        // Lookup Topic in Solace JNDI
        final Topic topic = (Topic)initialContext.lookup("marx_test_celer_orders_topic"); // felix_trading_order_otc_topic

        // Create a Producer Session
        final MessageProducer producer = session.createProducer(topic);

        try {
            for(int i = 0; i <= 1000; i +=3) {
                for (String fileName : new String[]{
                        "src/main/resources/samples/CELER_ORDER_ROUTING_TRADE_001.json",
                        "src/main/resources/samples/CELER_ORDER_ROUTING_TRADE_002.json",
                        "src/main/resources/samples/CELER_ORDER_ROUTING_TRADE_003.json"
                }) {
                    try (var reader = new FileReader(fileName)) {
                        var json = (JSONObject) (new JSONParser().parse(reader));

                        TextMessage message = session.createTextMessage(json.toString());
                        // message.setStringProperty("stringproperty", "stringvalue");
                        // message.setIntProperty("intproperty", 1);
                        message.setBooleanProperty(SupportedProperty.SOLACE_JMS_PROP_DEAD_MSG_QUEUE_ELIGIBLE, true);

                        System.out.printf("Sending request message '%s' to topic '%s'...%n", message.getText(), topic);
                        producer.send(topic, message, DeliveryMode.PERSISTENT, Message.DEFAULT_PRIORITY, Message.DEFAULT_TIME_TO_LIVE);
                        System.out.printf("Message sent successfully to topic '%s'...%n", topic);
                    } catch (IOException | ParseException e) {
                        throw new IOException(String.format("File '%s' does not exist.", fileName));
                    }
                }
            }
        } catch (javax.jms.JMSException e) {
            System.out.println(e.getMessage());
        } finally {
            System.out.println("Closing resources... ");

            if (producer != null ) {
                producer.close();
            }

            session.close();
            connection.close();
            initialContext.close();

            System.out.println("Closed.");
        }
    }
}
