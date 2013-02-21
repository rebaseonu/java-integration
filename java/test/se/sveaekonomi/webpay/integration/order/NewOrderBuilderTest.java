package se.sveaekonomi.webpay.integration.order;

import static org.junit.Assert.*;

import javax.xml.bind.ValidationException;

import org.junit.Test;

import se.sveaekonomi.webpay.integration.WebPay;
import se.sveaekonomi.webpay.integration.order.create.CreateOrderBuilder;
import se.sveaekonomi.webpay.integration.order.row.Item;
import se.sveaekonomi.webpay.integration.order.row.OrderRowBuilder;
import se.sveaekonomi.webpay.integration.util.constant.COUNTRYCODE;
import se.sveaekonomi.webpay.integration.webservice.svea_soap.SveaCreateOrder;
import se.sveaekonomi.webpay.integration.webservice.svea_soap.SveaRequest;


public class NewOrderBuilderTest {
    
  /*  @Test
    public void testInvoiceOrderWithOneRow() throws ValidationException {
        CreateOrderBuilder request = WebPay.createOrder()
        .setTestmode();

        OrderRowBuilder<?> row = Item.orderRow().setArticleNumber("1")
                .setQuantity(2)
                .setAmountExVat(100.00)
                .setDescription("Specification")
                .setName("Prod")
                .setUnit("st")
                .setVatPercent(25)
                .setDiscountPercent(0);
        request.addOrderRow(row);
        
        SveaRequest<SveaCreateOrder> sveaRequest = request.setCustomerSsn(194605092222L)
                .setCountryCode(COUNTRYCODE.SE)
                .setCustomerReference("33")
                .setOrderDate("2012-12-12")
                .setCurrency("SEK")
                .useInvoicePayment()
                .prepareRequest();
        
        assertEquals("194605092222", sveaRequest.request.CreateOrderInformation.CustomerIdentity.NationalIdNumber);
    }*/
    
    @Test
    public void testBuildOrderWithCompanyCustomer() throws ValidationException {
        CreateOrderBuilder createOrder = WebPay.createOrder()
                .setTestmode();
        createOrder.addOrderRow(Item.orderRow()
                .setArticleNumber("1")
                .setQuantity(2)
                .setAmountExVat(100.00)
                .setDescription("Specification")
                .setName("Prod")
                .setUnit("st")
                .setVatPercent(25)
                .setDiscountPercent(0));
        
        createOrder.addCustomerDetails(Item.companyCustomer()
                .setCompanyIdNumber("666666")
                .setEmail("test@svea.com")
                .setPhoneNumber(999999)
                .setIpAddress("123.123.123.123")
                .setStreetAddress("Gatan", 23)
                .setCoAddress("c/o Eriksson")
                .setZipCode("9999")
                .setLocality("Stan"));
        
        SveaRequest<SveaCreateOrder> request = createOrder.setCountryCode(COUNTRYCODE.SE)
                .setCustomerReference("33")
                .setOrderDate("2012-12-12")
                .setCurrency("SEK")                
                .useInvoicePayment()
                .prepareRequest();
        
        assertEquals("666666", request.request.CreateOrderInformation.CustomerIdentity.NationalIdNumber);
    }
}