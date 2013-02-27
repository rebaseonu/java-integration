package se.sveaekonomi.webpay.integration.hosted.payment;

import static org.junit.Assert.assertEquals;

import java.util.List;

import javax.xml.bind.ValidationException;

import org.junit.Before;
import org.junit.Test;

import se.sveaekonomi.webpay.integration.WebPay;
import se.sveaekonomi.webpay.integration.hosted.helper.PaymentForm;
import se.sveaekonomi.webpay.integration.order.VoidValidator;
import se.sveaekonomi.webpay.integration.order.create.CreateOrderBuilder;
import se.sveaekonomi.webpay.integration.order.row.Item;
import se.sveaekonomi.webpay.integration.util.constant.COUNTRYCODE;
import se.sveaekonomi.webpay.integration.util.constant.PAYMENTMETHOD;
import se.sveaekonomi.webpay.integration.util.security.Base64Util;

public class CardPaymentTest {
    
    private CreateOrderBuilder order;
    
    @Before
    public void setUp() {
        order = WebPay.createOrder()
        		.setValidator(new VoidValidator());
    }    
    
    @Test
    public void testConfigureExcludedPaymentMethodsSe() throws ValidationException {
    	List<PAYMENTMETHOD> excluded = order.setCountryCode(COUNTRYCODE.SE)
        .usePayPageCardOnly()
        .configureExcludedPaymentMethods()
        .getExcludedPaymentMethods();
    	
        /*CardPayment payment = new CardPayment(order);
        
        payment.configureExcludedPaymentMethods();
        List<PAYMENTMETHOD> excluded = payment.getExcludedPaymentMethods();
        */
        assertEquals(21, excluded.size());
    }
    
    @Test
    public void testBuildCardPayment() throws Exception {
    	PaymentForm form = order.addOrderRow(Item.orderRow()
			.setAmountExVat(100.00)
			.setArticleNumber("1")
			.setQuantity(2)
			.setUnit("st")
			.setDescription("Specification")
			.setVatPercent(25)
			.setDiscountPercent(0)
			.setName("Prod"))
			
			.addCustomerDetails(Item.companyCustomer()
			.setVatNumber("2345234")
			.setCompanyName("TestCompagniet"))
			
			.addFee(Item.shippingFee()
			.setShippingId("33")
			.setName("shipping")
			.setDescription("Specification")
			.setAmountExVat(50)
			.setUnit("st")
			.setVatPercent(25)
			.setDiscountPercent(0))
			   
			.addDiscount(Item.relativeDiscount()
			.setDiscountId("1")
			.setName("Relative")
			.setDescription("RelativeDiscount")
			.setUnit("st")               
			.setDiscountPercent(50))
			      
			.setCountryCode(COUNTRYCODE.SE)
			.setOrderDate("2012-12-12")
			.setClientOrderNumber("33")
			.setCurrency("SEK")
			.usePayPageCardOnly()
			.setReturnUrl("http://myurl.se")
            .getPaymentForm();
                     
        String xml = form.getMessageAsXML();
        String amount = xml.substring(xml.indexOf("<amount>") + 8, xml.indexOf("</amount>"));
        String vat = xml.substring(xml.indexOf("<vat>") + 5, xml.indexOf("</vat"));
        //<?xml version="1.0" encoding="UTF-8"?><!--Message generated by Integration package Java--><payment><customerRefNo>33</customerRefNo><returnurl>http://myurl.se</returnurl><currency>SEK</currency><amount>18750</amount><vat>3750</vat><orderrows><row><sku>1</sku><name>Prod</name><description>Specification</description><amount>12500</amount><vat>2500</vat><quantity>2</quantity><unit>st</unit></row><row><sku>33</sku><name>shipping</name><description>Specification</description><amount>6250</amount><vat>1250</vat><quantity>1</quantity><unit>st</unit></row><row><sku>1</sku><name>Name</name><description>RelativeDiscount</description><amount>-12500</amount><vat>-2500</vat><quantity>1</quantity><unit>st</unit></row></orderrows><excludepaymentmethods><exclude>PAYPAL</exclude><exclude>DBNORDEASE</exclude><exclude>DBSEBSE</exclude><exclude>DBSEBFTGSE</exclude><exclude>DBSHBSE</exclude><exclude>DBSWEDBANKSE</exclude><exclude>SVEAINVOICESE</exclude><exclude>SVEASPLITSE</exclude><exclude>SVEAINVOICEEU_SE</exclude><exclude>SVEASPLITEU_SE</exclude></excludepaymentmethods></payment>
        assertEquals("18750", amount);
        assertEquals("3750", vat);
        
    }   
    
    @Test
    public void testSetAuthorization() throws Exception {
        order = WebPay.createOrder()
                .setTestmode()                
       .addOrderRow(Item.orderRow()
                .setAmountExVat(100.00)
                .setArticleNumber("1")
                .setQuantity(2)
                .setUnit("st")
                .setDescription("Specification")
                .setVatPercent(25)
                .setDiscountPercent(0)
                .setName("Prod"))
          
       .addFee(Item.shippingFee()
                  .setShippingId("33")
                  .setName("shipping")
                  .setDescription("Specification")
                  .setAmountExVat(50)
                  .setUnit("st")
                  .setVatPercent(25)
                  .setDiscountPercent(0))
          
       .addFee(Item.invoiceFee()
                  .setName("Svea fee")
                  .setDescription("Fee for invoice")
                  .setAmountExVat(50)
                  .setUnit("st")
                  .setVatPercent(25)
                  .setDiscountPercent(0))
       .addDiscount(Item.relativeDiscount()
                  .setName("Svea fee")
                  .setDescription("Fee for invoice")
                  .setUnit("st")               
                  .setDiscountPercent(0))
              
       .addCustomerDetails(Item.companyCustomer()
                  .setVatNumber("2345234")
                  .setCompanyName("TestCompagniet"));
      
        PaymentForm form = order
                .setCountryCode(COUNTRYCODE.SE)
                .setOrderDate("2012-12-12")
                .setClientOrderNumber("33")
                .setCurrency("SEK")
                .usePayPageCardOnly()
                .setMerchantIdBasedAuthorization(4444, "secret")
                .setReturnUrl("http://myurl.se")
                .getPaymentForm();
        
        assertEquals(form.getMerchantId(), "4444");
        assertEquals(form.getSecretWord(), "secret");
    }
}
