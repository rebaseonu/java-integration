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
import se.sveaekonomi.webpay.integration.util.constant.CURRENCY;
import se.sveaekonomi.webpay.integration.util.test.TestingTool;

public class CardPaymentTest {

    private CreateOrderBuilder order;
    
    @Before
    public void setUp() {
        order = WebPay.createOrder()
                .setValidator(new VoidValidator());
    }
    
    @Test
    public void testConfigureExcludedPaymentMethodsSe() throws ValidationException {
        List<String> excluded = order
                .setCountryCode(COUNTRYCODE.SE)
                .usePayPageCardOnly()
                .configureExcludedPaymentMethods()
                .getExcludedPaymentMethods();
        
        assertEquals(21, excluded.size());
    }
    
    @Test
    public void testBuildCardPayment() throws Exception {
        PaymentForm form = order.addOrderRow(TestingTool.createOrderRow())
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
            .setCurrency(CURRENCY.SEK)
            .usePayPageCardOnly()
            .setReturnUrl("http://myurl.se")
            .getPaymentForm();
        
        String xml = form.getXmlMessage();
        String amount = xml.substring(xml.indexOf("<amount>") + 8, xml.indexOf("</amount>"));
        String vat = xml.substring(xml.indexOf("<vat>") + 5, xml.indexOf("</vat"));
        
        assertEquals("18750", amount);
        assertEquals("3750", vat);
    }
    
    @Test
    public void testBuildCardPaymentDE() throws Exception {
        PaymentForm form = order.addOrderRow(TestingTool.createOrderRow())
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
            
            .setCountryCode(COUNTRYCODE.DE)
            .setOrderDate("2012-12-12")
            .setClientOrderNumber("33")
            .setCurrency(CURRENCY.SEK)
            .usePayPageCardOnly()
            .setReturnUrl("http://myurl.se")
            .getPaymentForm();
        
        String xml = form.getXmlMessage();
        String amount = xml.substring(xml.indexOf("<amount>") + 8, xml.indexOf("</amount>"));
        String vat = xml.substring(xml.indexOf("<vat>") + 5, xml.indexOf("</vat"));
        
        assertEquals("18750", amount);
        assertEquals("3750", vat);
    }
    
    @Test
    public void testSetAuthorization() throws Exception {
        PaymentForm form = WebPay.createOrder()
            .addOrderRow(TestingTool.createOrderRow())
        
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
                .setCompanyName("TestCompagniet"))
            
            .setCountryCode(COUNTRYCODE.SE)
            .setOrderDate("2012-12-12")
            .setClientOrderNumber("33")
            .setCurrency(CURRENCY.SEK)
            .usePayPageCardOnly()
            .setReturnUrl("http://myurl.se")
            .getPaymentForm();
        
        assertEquals("1130", form.getMerchantId());
        assertEquals("8a9cece566e808da63c6f07ff415ff9e127909d000d259aba24daa2fed6d9e3f8b0b62e8ad1fa91c7d7cd6fc3352deaae66cdb533123edf127ad7d1f4c77e7a3", form.getSecretWord());
    }
}
