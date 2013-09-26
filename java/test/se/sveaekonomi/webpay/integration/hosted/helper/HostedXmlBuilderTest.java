package se.sveaekonomi.webpay.integration.hosted.helper;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import se.sveaekonomi.webpay.integration.WebPay;
import se.sveaekonomi.webpay.integration.hosted.payment.FakeHostedPayment;
import se.sveaekonomi.webpay.integration.order.create.CreateOrderBuilder;
import se.sveaekonomi.webpay.integration.order.row.Item;
import se.sveaekonomi.webpay.integration.util.constant.COUNTRYCODE;
import se.sveaekonomi.webpay.integration.util.constant.CURRENCY;
import se.sveaekonomi.webpay.integration.util.constant.LANGUAGECODE;
import se.sveaekonomi.webpay.integration.util.constant.PAYMENTMETHOD;

public class HostedXmlBuilderTest {

    HostedXmlBuilder xmlBuilder;
    private CreateOrderBuilder order;
    String xml;
    
    @Before
    public void setUp() {
        xmlBuilder = new HostedXmlBuilder();
        xml = "";
    }
    
    @Test
    public void testBasicXml() throws Exception {
        order = WebPay.createOrder()
                .setCountryCode(COUNTRYCODE.SE)
                .setCurrency(CURRENCY.SEK)
                .setClientOrderNumber("nr26")
                .addOrderRow(Item.orderRow()
                        .setAmountExVat(4)
                        .setVatPercent(25)
                        .setQuantity(1))
                .addCustomerDetails(Item.individualCustomer()
                        .setNationalIdNumber("666666"));
        
        FakeHostedPayment payment = new FakeHostedPayment(order);
        payment.setReturnUrl("http://myurl.se") 
            .calculateRequestValues();
        
        try {
            xml = xmlBuilder.getXml(payment);
        } catch (Exception e) {
            throw e;
        }
        
        final String EXPECTED_XML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><!--Message generated by Integration package Java--><payment><customerrefno>nr26</customerrefno><currency>SEK</currency><amount>500</amount><vat>100</vat><returnurl>http://myurl.se</returnurl><iscompany>false</iscompany><customer><ssn>666666</ssn><country>SE</country></customer><orderrows><row><sku></sku><name></name><description></description><amount>500</amount><vat>100</vat><quantity>1</quantity></row></orderrows><addinvoicefee>false</addinvoicefee></payment>";
        assertEquals(EXPECTED_XML, xml);
    }
    
    @Test
    public void testXmlWithIndividualCustomer() throws Exception {
        order = WebPay.createOrder()
                .setCountryCode(COUNTRYCODE.SE)
                .setCurrency(CURRENCY.SEK)
                .setClientOrderNumber("nr26")
                .addOrderRow(Item.orderRow()
                        .setAmountExVat(4)
                        .setVatPercent(25)
                        .setQuantity(1))
                .addCustomerDetails(Item.individualCustomer()
                        .setName("Julius", "Caesar")
                        .setInitials("JS")
                        .setNationalIdNumber("666666")
                        .setPhoneNumber("999999")
                        .setEmail("test@svea.com")
                        .setIpAddress("123.123.123.123")
                        .setStreetAddress("Gatan", "23")
                        .setCoAddress("c/o Eriksson")
                        .setZipCode("9999")
                        .setLocality("Stan"));
        
        FakeHostedPayment payment = new FakeHostedPayment(order);
        payment.setReturnUrl("http://myurl.se")
                .calculateRequestValues();
        
        try {
            xml = xmlBuilder.getXml(payment);
        } catch (Exception e) {
            throw e;
        }
        
        assertTrue(xml.contains("<customer><ssn>666666</ssn><firstname>Julius</firstname><lastname>Caesar</lastname><initials>JS</initials><phone>999999</phone><email>test@svea.com</email><address>Gatan</address><housenumber>23</housenumber><address2>c/o Eriksson</address2><zip>9999</zip><city>Stan</city><country>SE</country></customer>"));
    }
    
    @Test
    public void testXmlWithCompanyCustomer() throws Exception {
        order = WebPay.createOrder()
                .setCountryCode(COUNTRYCODE.SE)
                .setCurrency(CURRENCY.SEK)
                .setClientOrderNumber("nr26")
                .addOrderRow(Item.orderRow()
                        .setAmountExVat(4)
                        .setVatPercent(25)
                        .setQuantity(1))
                .addCustomerDetails(Item.companyCustomer()
                        .setCompanyName("TestCompagniet")
                        .setNationalIdNumber("666666")
                        .setPhoneNumber("999999")
                        .setEmail("test@svea.com")
                        .setIpAddress("123.123.123.123")
                        .setStreetAddress("Gatan", "23")
                        .setCoAddress("c/o Eriksson")
                        .setZipCode("9999")
                        .setLocality("Stan"));
        
        FakeHostedPayment payment = new FakeHostedPayment(order);
        payment.setReturnUrl("http://myurl.se") 
            .calculateRequestValues();
        
        try {
            xml = xmlBuilder.getXml(payment);
        } catch (Exception e) {
            throw e;
        }
        
        assertTrue(xml.contains("<customer><ssn>666666</ssn><firstname>TestCompagniet</firstname><phone>999999</phone><email>test@svea.com</email><address>Gatan</address><housenumber>23</housenumber><address2>c/o Eriksson</address2><zip>9999</zip><city>Stan</city><country>SE</country></customer>"));
    }
    
    @Test
    public void testXmlCancelUrl() throws Exception {
        order = WebPay.createOrder() 
                .setCountryCode(COUNTRYCODE.SE)
                .setCurrency(CURRENCY.SEK)
                .setClientOrderNumber("nr26")
                .addOrderRow(Item.orderRow()
                        .setQuantity(1)
                        .setAmountExVat(4)
                        .setAmountIncVat(5))
                .addCustomerDetails(Item.companyCustomer());
        
        FakeHostedPayment payment = new FakeHostedPayment(order);
        payment.setCancelUrl("http://www.cancel.com")
            .setReturnUrl("http://myurl.se") 
            .calculateRequestValues();
        
        try {
            xml = xmlBuilder.getXml(payment);
        } catch (Exception e) {
            throw e;
        }
        
        assertTrue(xml.contains("<cancelurl>http://www.cancel.com</cancelurl>"));
    }
    
    @Test
    public void testOrderRowXml() throws Exception {
        order = WebPay.createOrder() 
            .addOrderRow(Item.orderRow()
                    .setArticleNumber("0")
                    .setName("Product")
                    .setDescription("Good product")
                    .setAmountExVat(4)
                    .setVatPercent(25)
                    .setQuantity(1)
                    .setUnit("kg"))
            .addCustomerDetails(Item.companyCustomer())
            .setCountryCode(COUNTRYCODE.SE)
            .setCurrency(CURRENCY.SEK)
            .setClientOrderNumber("nr26");
        
        FakeHostedPayment payment = new FakeHostedPayment(order);
        payment.setPayPageLanguageCode(LANGUAGECODE.sv)
            .setReturnUrl("http://myurl.se")
            .calculateRequestValues();
        
        try {
            xml = xmlBuilder.getXml(payment);
        } catch (Exception e) {
            throw e;
        }
        
        assertTrue(xml.contains("<orderrows><row><sku>0</sku><name>Product</name><description>Good product</description><amount>500</amount><vat>100</vat><quantity>1</quantity><unit>kg</unit></row></orderrows>"));
    }
    
    @Test
    public void testDirectPaymentSpecificXml() throws Exception {
        xml = WebPay.createOrder()
                .setCountryCode(COUNTRYCODE.SE)
                .setCurrency(CURRENCY.SEK)
                .setClientOrderNumber("nr26")
                .addOrderRow(Item.orderRow()
                        .setQuantity(1)
                        .setAmountExVat(4)
                        .setAmountIncVat(5))
                .addCustomerDetails(Item.companyCustomer())
                .usePayPageDirectBankOnly()
                .setReturnUrl("https://test.sveaekonomi.se/webpay/admin/merchantresponsetest.xhtm")
                .getPaymentForm()
                .getXmlMessage();
        
        assertTrue(xml.contains("<excludepaymentmethods><exclude>BANKAXESS</exclude><exclude>PAYPAL</exclude><exclude>KORTCERT</exclude><exclude>SKRILL</exclude><exclude>SVEAINVOICESE</exclude><exclude>SVEAINVOICEEU_SE</exclude><exclude>SVEASPLITSE</exclude><exclude>SVEASPLITEU_SE</exclude><exclude>SVEAINVOICEEU_DE</exclude><exclude>SVEASPLITEU_DE</exclude><exclude>SVEAINVOICEEU_DK</exclude><exclude>SVEASPLITEU_DK</exclude><exclude>SVEAINVOICEEU_FI</exclude><exclude>SVEASPLITEU_FI</exclude><exclude>SVEAINVOICEEU_NL</exclude><exclude>SVEASPLITEU_NL</exclude><exclude>SVEAINVOICEEU_NO</exclude><exclude>SVEASPLITEU_NO</exclude></excludepaymentmethods>"));
    }
    
    @Test
    public void testCardPaymentSpecificXml() throws Exception {
        xml = WebPay.createOrder()
                .setCountryCode(COUNTRYCODE.SE)
                .setCurrency(CURRENCY.SEK)
                .setClientOrderNumber("nr26")
                .addOrderRow(Item.orderRow()
                        .setQuantity(1)
                        .setAmountExVat(4)
                        .setAmountIncVat(5))
                .addCustomerDetails(Item.companyCustomer())
                .usePayPageCardOnly()
                .setReturnUrl("https://test.sveaekonomi.se/webpay/admin/merchantresponsetest.xhtm")
                .getPaymentForm()
                .getXmlMessage();
        
        assertTrue(xml.contains("<excludepaymentmethods><exclude>PAYPAL</exclude><exclude>DBNORDEASE</exclude><exclude>DBSEBSE</exclude><exclude>DBSEBFTGSE</exclude><exclude>DBSHBSE</exclude><exclude>DBSWEDBANKSE</exclude><exclude>BANKAXESS</exclude><exclude>SVEAINVOICESE</exclude><exclude>SVEAINVOICEEU_SE</exclude><exclude>SVEASPLITSE</exclude><exclude>SVEASPLITEU_SE</exclude><exclude>SVEAINVOICEEU_DE</exclude><exclude>SVEASPLITEU_DE</exclude><exclude>SVEAINVOICEEU_DK</exclude><exclude>SVEASPLITEU_DK</exclude><exclude>SVEAINVOICEEU_FI</exclude><exclude>SVEASPLITEU_FI</exclude><exclude>SVEAINVOICEEU_NL</exclude><exclude>SVEASPLITEU_NL</exclude><exclude>SVEAINVOICEEU_NO</exclude><exclude>SVEASPLITEU_NO</exclude></excludepaymentmethods>"));
    }
    
    @Test
    public void testPayPagePaymentSpecificXmlNullPaymentMethod() throws Exception {
        xml = WebPay.createOrder()
                .setCountryCode(COUNTRYCODE.SE)
                .setCurrency(CURRENCY.SEK)
                .setClientOrderNumber("nr26")
                .addOrderRow(Item.orderRow()
                        .setQuantity(1)
                        .setAmountExVat(4)
                        .setAmountIncVat(5))
                .addCustomerDetails(Item.companyCustomer())
                .usePayPage()
                .setReturnUrl("https://test.sveaekonomi.se/webpay/admin/merchantresponsetest.xhtm")
                .getPaymentForm()
                .getXmlMessage();

        assertFalse(xml.contains("<paymentmethod>"));
        assertFalse(xml.contains("<excludepaymentmethods>"));
    }
    
    @Test
    public void testPayPagePaymentSetLanguageCode() throws Exception {
        xml = WebPay.createOrder()
                .setCountryCode(COUNTRYCODE.SE)
                .setCurrency(CURRENCY.SEK)
                .setClientOrderNumber("nr26")
                .addOrderRow(Item.orderRow()
                        .setQuantity(1)
                        .setAmountExVat(4)
                        .setAmountIncVat(5))
                .addCustomerDetails(Item.companyCustomer())
                .usePayPage()
                .setPayPageLanguage(LANGUAGECODE.sv)
                .setReturnUrl("https://test.sveaekonomi.se/webpay/admin/merchantresponsetest.xhtm")
                .getPaymentForm()
                .getXmlMessage();
        
        assertTrue(xml.contains("<lang>sv</lang>"));
    }
    
    @Test
    public void testPayPagePaymentPayPal() throws Exception {
        xml = WebPay.createOrder()
                .setCountryCode(COUNTRYCODE.SE)
                .setCurrency(CURRENCY.SEK)
                .setClientOrderNumber("nr26")
                .addOrderRow(Item.orderRow()
                        .setQuantity(1)
                        .setAmountExVat(4)
                        .setAmountIncVat(5))
                        .addCustomerDetails(Item.companyCustomer())
                .usePayPage()
                .setReturnUrl("https://test.sveaekonomi.se/webpay/admin/merchantresponsetest.xhtm")
                .setPaymentMethod(PAYMENTMETHOD.PAYPAL)
                .getPaymentForm()
                .getXmlMessage();
        
        assertTrue(xml.contains("<paymentmethod>PAYPAL</paymentmethod>"));
    }
    
    @Test
    public void testPayPagePaymentSpecificXml() throws Exception {
        xml = WebPay.createOrder()
                .setCountryCode(COUNTRYCODE.SE)
                .setCurrency(CURRENCY.SEK)
                .setClientOrderNumber("nr26")
                .addOrderRow(Item.orderRow()
                        .setQuantity(1)
                        .setAmountExVat(4)
                        .setAmountIncVat(5))
                .addCustomerDetails(Item.companyCustomer())
                .usePayPage()  
                .setReturnUrl("https://test.sveaekonomi.se/webpay/admin/merchantresponsetest.xhtm")
                .setPaymentMethod(PAYMENTMETHOD.INVOICE)
                .getPaymentForm()
                .getXmlMessage();
        
        assertTrue(xml.contains("<paymentmethod>SVEAINVOICEEU_SE</paymentmethod>"));
    }
}
