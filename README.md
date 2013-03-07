# Java Integration Package API for SveaWebPay

*NOTE:* This documentation is not yet fully converted from PHP. The general flow and methods should be all the same. Let the fluent API guide you using code completion in your IDE, and you should be fairly well on the way. Sorry for any inconvenience.

| Branch                            | Build status                               |
|---------------------------------- |------------------------------------------- |
| master (latest release)           | [![Build Status](https://travis-ci.org/sveawebpay/java-integration.png?branch=master)](https://travis-ci.org/sveawebpay/java-integration) |
| develop                           | [![Build Status](https://travis-ci.org/sveawebpay/java-integration.png?branch=develop)](https://travis-ci.org/sveawebpay/java-integration) |

## Index
* [Introduction](https://github.com/sveawebpay/java-integration/tree/develop#introduction)
* [Build](https://github.com/sveawebpay/java-integration/tree/develop#build)
* [Configuration](https://github.com/sveawebpay/java-integration/tree/develop#configuration)
* [1. CreateOrder](https://github.com/sveawebpay/java-integration/tree/develop#1-createorder)
    * [Test mode](https://github.com/sveawebpay/java-integration/tree/develop#11-test-mode)
    * [Specify order](https://github.com/sveawebpay/java-integration/tree/develop#12-specify-order)
    * [Customer identity](https://github.com/sveawebpay/java-integration/tree/develop#13-customer-identity)
    * [Other values](https://github.com/sveawebpay/java-integration/tree/develop#14-other-values)
    * [Choose payment](https://github.com/sveawebpay/java-integration/tree/develop#15-choose-payment)
* [2. GetPaymentPlanParams](https://github.com/sveawebpay/java-integration/tree/develop#2-getpaymentplanparams)
* [3. GetAddresses](https://github.com/sveawebpay/java-integration/tree/develop#2-getpaymentplanparams)
* [4. DeliverOrder](https://github.com/sveawebpay/java-integration/tree/develop#2-getpaymentplanparams)
    * [Test mode](https://github.com/sveawebpay/java-integration/tree/develop#41-testmode)
    * [Specify order](https://github.com/sveawebpay/java-integration/tree/develop#42-specify-order)
    * [Other values](https://github.com/sveawebpay/java-integration/tree/develop#43-other-values)
* [5. CloseOrder](https://github.com/sveawebpay/java-integration/tree/develop#5-closeorder)
* [6. Response handler](https://github.com/sveawebpay/java-integration/tree/develop#6-response-handler)
* [APPENDIX](https://github.com/sveawebpay/java-integration/tree/develop#appendix)


## Introduction                                                             
This integration package is built for developers to simplify the integration of Svea WebPay services. 
Using this package will make your implementation sustainable and unaffected for changes
in our payment system. Just make sure to update the package regularly.

The API is built as a *Fluent API* so you can use *method chaining* when implementing it in your code.

[<< To top](https://github.com/sveawebpay/java-integration/tree/develop#java-integration-package-api-for-sveawebpay)

## Build                                                             

To build a jar file, use the ant build file located at `java/build.xml`. Use the command
```
ant clean-jar
``` 
to build, unit test, and create the `sveawebpay.jar` file (in the `target/jar` directory), or preferrably 
```
ant clean-integrationtest
``` 
that will build the jar and also run the integration tests on the created jar. 


Other public targets can be found in the build.xml file.


[<< To top](https://github.com/sveawebpay/java-integration/tree/develop#java-integration-package-api-for-sveawebpay)

## Configuration 


[<< To top](https://github.com/sveawebpay/java-integration/tree/develop#java-integration-package-api-for-sveawebpay)

## 1. createOrder                                                            
Creates an order and performs payment for all payment forms. Invoice and payment plan will perform 
a synchronous payment and return a response. 
Other hosted payments, like card, direct bank and other payments from the *PayPage*,
on the other hand are asynchronous. They will return an html form with formatted message to send from your store.
For every new payment type implementation, you follow the steps from the beginning and chose your payment type preffered in the end:
Build order -> choose payment type -> doRequest/getPaymentForm

```java
CreateOrderResponse response = WebPay.createOrder()               
//If test mode
.setTestmode()
//For all products and other items
.addOrderRow(Item.orderRow()...)
//If shipping fee
.addFee(Item.shippingFee()...)
//If invoice with invoice fee
.addFee(Item.invoiceFee()...)
//If discount or coupon with fixed amount
.addDiscount(Item.fixedDiscount()...)
//If discount or coupon with percent discount
.addDiscount(Item.relativeDiscount()...)
//Individual customer values
.addCustomerDetails(Item.individualCustomer()...)
//Company customer values
.addCustomerDetails(Item.companyCustomer()...)
//Other values
.setCountryCode(COUNTRYCODE.SE)
.setOrderDate("2012-12-12")
.setCustomerReference("33")
.setClientOrderNumber("nr26")
.setCurrency("SEK")
.setAddressSelector("7fd7768")

//Continue by choosing one of the following paths
    //Continue as a card payment
    .usePayPageCardOnly() 
        ...
    .getPaymentForm();
    //Continue as a direct bank payment		
    .usePayPageDirectBankOnly()
        ...
    .getPaymentForm();
    //Continue as a PayPage payment
    .usePayPage()
        ...
    .getPaymentForm();
    //Continue as a PayPage payment
    .usePaymentMethod (PaymentMethod.DBSEBSE) //see APPENDIX for Constants
        ...
    .getPaymentForm();
    //Continue as an invoice payment
    .useInvoicePayment()
    ...
    .doRequest();
    //Continue as a payment plan payment
    .usePaymentPlanPayment("campaigncode", false)
    ...
    .doRequest();
```
[<< To top](https://github.com/sveawebpay/java-integration/tree/develop#java-integration-package-api-for-sveawebpay)

### 1.1 Test mode                                                            
Set test mode while developing to make the calls to our test server.
Remove when you change to production mode.	
```java
    .setTestmode()
```
[<< To top](https://github.com/sveawebpay/java-integration/tree/develop#java-integration-package-api-for-sveawebpay)
	
### 1.2 Specify order                                                        
Continue by adding values for products and other. You can add order row, fee and discount. Chose the right Item object as parameter.
You can use the *add* functions with an Item object or a List of Item objects as parameters. 

```java
.addOrderRow(Item.orderRow(). ...)

//or
List<OrderRowBuilder> orderRows = new ArrayList<OrderRowBuilder>(); //or use another preferrable Collection
orderRows.add(Item.orderRow(). ...)
...
createOrder.addOrderRows(orderRows);
```
	
#### 1.2.1 OrderRow
All products and other items. It�s required to have a minimum of one order row.
**The price can be set in a combination by using a minimum of two out of three functions: setAmountExVat(), setAmountIncVat() and setVatPercent().**
```java
.addOrderRow(Item.orderRow()     
        .setQuantity(2)                        //Required
        .setAmountExVat(100.00)                //Optional, see info above
        .setAmountIncVat(125.00)               //Optional, see info above
        .setVatPercent(25)                     //Optional, see info above
        .setArticleNumber("1")                 //Optional
        .setDescription("Specification")       //Optional
        .setName("Prod")                       //Optional
        .setUnit("st")                         //Optional              
        .setDiscountPercent(0)                 //Optional
    )
```

#### 1.2.2 ShippingFee
**The price can be set in a combination by using a minimum of two out of three functions: setAmountExVat(), setAmountIncVat()and setVatPercent().**
```java
.addFee(Item.shippingFee()
        .setAmountExVat(50)                    //Optional, see info above
        .setAmountIncVat(62.50)                //Optional, see info above
        .setVatPercent(25)                     //Optional, see info above
		.setShippingId("33")                   //Optional
        .setName("shipping")                   //Optional
        .setDescription("Specification")       //Optional
        .setUnit("st")                         //Optional             
        .setDiscountPercent(0)                 //Optional
   )
```
#### 1.2.3 InvoiceFee
**The price can be set in a combination by using a minimum of two out of three functions: setAmountExVat(), setAmountIncVat() and setVatPercent().**
```java
.addFee(Item.invoiceFee()
        .setAmountExVat(50)                    //Optional, see info above
        .setAmountIncVat(62.50)                //Optional, see info above
        .setVatPercent(25)                     //Optional, see info above
        .setName("Svea fee")                   //Optional
        .setDescription("Fee for invoice")     //Optional		
        .setUnit("st")                         //Optional
        .setDiscountPercent(0)                 //Optional
    )
```
#### 1.2.4 Fixed Discount
When discount or coupon is a fixed amount on total product amount.
```java
.addDiscount(Item.fixedDiscount()                
        .setAmountIncVat(100.00)               //Required
        .setDiscountId("1")                    //Optional        
        .setUnit("st")                         //Optional
        .setDescription("FixedDiscount")       //Optional
        .setName("Fixed")                      //Optional
    )
```
#### 1.2.5 Relative Discount
When discount or coupon is a percentage on total product amount.
```java
.addDiscount(Item.relativeDiscount()
        .setDiscountPercent(50)                //Required
        .setDiscountId("1")                    //Optional      
        .setUnit("st")                         //Optional
        .setName("Relative")                   //Optional
        .setDescription("RelativeDiscount")    //Optional
    )
```
[<< To top](https://github.com/sveawebpay/java-integration/tree/develop#java-integration-package-api-for-sveawebpay)

### 1.3 Customer Identity   
Customer identity is required for invoice and payment plan orders. Required values varies 
depending on country and customer type. For SE, NO, DK and FI national id number is required. Email and ip address are desirable.

####1.3.1 Options for individual customers
```java
.addCustomerDetails(Item.individualCustomer()
    .setNationalIdNumber(194605092222) //Required for individual customers in SE, NO, DK, FI
    .setInitials("SB")                 //Required for individual customers in NL 
    .setBirthDate(1923, 12, 12)        //Required for individual customers in NL and DE
    .setName("Tess", "Testson")        //Required for individual customers in NL and DE    
    .setStreetAddress("Gatan", 23)     //Required in NL and DE    
    .setZipCode(9999)                  //Required in NL and DE
    .setLocality("Stan")               //Required in NL and DE    
    .setEmail("test@svea.com")         //Optional but desirable    
    .setIpAddress("123.123.123")       //Optional but desirable
    .setCoAddress("c/o Eriksson")      //Optional
    .setPhoneNumber(999999)            //Optional
    )
```

####1.3.2 Options for company customers
```java
.addCustomerDetails(Item.companyCustomer()
    .setNationalIdNumber("2345234")		//Required for company customers in SE, NO, DK, FI
    .setVatNumber("NL2345234")			//Required for NL and DE
    .setCompanyName("TestCompagniet")  	//Required for Eu countries like NL and DE
    )
```
[<< To top](https://github.com/sveawebpay/java-integration/tree/develop#java-integration-package-api-for-sveawebpay)

### 1.4 Other values  
```java
.setCountryCode(COUNTRYCODE.SE)                   //Required for synchronous payments    
.setCurrency("SEK")                     //Required for card payment, direct payment and PayPage payment.
.setClientOrderNumber("nr26")           //Required for card payment, direct payment, PaymentMethod payment and PayPage payments.
.setAddressSelector("7fd7768")          //Optional. Recieved from getAddresses
.setOrderDate("2012-12-12")             //Required for synchronous payments
.setCustomerReference("33")             //Optional
```
[<< To top](https://github.com/sveawebpay/java-integration/tree/develop#java-integration-package-api-for-sveawebpay)

### 1.5 Choose payment 
End process by choosing the payment method you desire.

Invoice and payment plan will perform a synchronous payment and return an object as response. 

Other hosted payments(card, direct bank and other payments from the *PayPage*)
on the other hand are asynchronous. They will return an html form with formatted message to send from your store.
The response is then returned to the return url you have specified in function setReturnUrl(). If you
use class *Response* with the xml response as parameter, you will receive a
formatted object as well. 

#### Asynchronous payments - Hosted solutions
Build order and recieve a *PaymentForm* object. Send the *PaymentForm* parameters: *merchantid*, *xmlMessageBase64* and *mac* by POST to
SveaConfig.SWP_TEST_URL or SveaConfig.SWP_PROD_URL. The *PaymentForm* object also contains a complete html form as string 
and the html form element as array.

```html
    <form name="paymentForm" id="paymentForm" method="post" action="SveaConfig.SWP_TEST_URL">
        <input type="hidden" name="merchantid" value=" + merchantId + " />
        <input type="hidden" name="message" value=" + xmlMessageBase64 + " />
        <input type="hidden" name="mac" value=" + mac + "/>
        <noscript><p>Javascript is inactivated.</p></noscript>
        <input type="submit" name="submit" value="Submit" />
    </form>
```


#### 1.5.1 PayPage with card payment options
*PayPage* with availible card payments only.

##### 1.5.1.1 Request
If Config/SveaConfig.php is not modified you can set your store authorization here.
```java
PaymentForm form = WebPay.createOrder()
	.addOrderRow(Item.orderRow()
        .setArticleNumber("1")
        .setQuantity(2)
        .setAmountExVat(100.00)
        .setDescription("Specification")
        .setName("Prod")
        .setUnit("st")
        .setVatPercent(25)
        .setDiscountPercent(0))
		
    .setCountryCode(COUNTRYCODE.SE)
    .setClientOrderNumber("33")
    .setOrderDate("2012-12-12")
    .setCurrency("SEK")
    .usePayPageCardOnly()
		.setMerchantIdBasedAuthorization(1200, "f78hv9")   //Optional
		.setReturnUrl("http://myurl.se")                   //Required
		.setCancelUrl("http://myurl.se")                   //Optional
		.getPaymentForm();

```
##### 1.5.1.2 Return
The values of *xmlMessageBase64*, *merchantid* and *mac* are to be sent as xml to SveaWebPay.
Function getPaymentForm() returns object type *PaymentForm* with accessible members:

| Value                 | Returns    | Description                               |
|-----------------------|----------- |-------------------------------------------|
| getXmlMessageBase64() | String     | Payment information in XML-format, Base64 encoded.| 
| getXmlMessage()       | String     | Payment information in XML-format.        |
| getMerchantId()       | String     | Authorization                             |
| getSecretWord()       | String     | Authorization                             |
| getMacSha512()        | String     | Message authentication code.              |    
| getCompleteForm()		| String     | A complete Html form with method= "post" with submit button to include in your code. |
| getFormHtmlFields()   | Map<String, String>   | Map with html tags as keys with of Html form fields to include. |
            
```java
PaymentForm form = ...
    .getPaymentForm();
```

#### 1.5.2 PayPage with direct bank payment options
*PayPage* with available direct bank payments only.
                
##### 1.5.2.1 Request
If Config/SveaConfig.php is not modified you can set your store authorization here.
```java
PaymentForm form = WebPay.createOrder()
.setTestmode()
.addOrderRow(Item.orderRow()
	.setArticleNumber("1")
	.setQuantity(2)
	.setAmountExVat(100.00)
	.setDescription("Specification")
	.setName("Prod")
	.setUnit("st")
	.setVatPercent(25)
	.setDiscountPercent(0))
		
    .setCountryCode(COUNTRYCODE.SE)
    .setCustomerReference("33")
    .setOrderDate("2012-12-12")
    .setCurrency("SEK")
	.usePayPageDirectBankOnly()
		.setReturnUrl("http://myurl.se")                   //Required	
		.setMerchantIdBasedAuthorization(1200, "f78hv9")   //Optional
		.setCancelUrl("http://myurl.se")                   //Optional
		.getPaymentForm();
```
##### 1.5.2.2 Return
Returns object type PaymentForm:
           
| Value                 | Returns    | Description                               |
|-----------------------|----------- |-------------------------------------------|
| getXmlMessageBase64() | String     | Payment information in XML-format, Base64 encoded.| 
| getXmlMessage()       | String     | Payment information in XML-format.        |
| getMerchantId()       | String     | Authorization                             |
| getSecretWord()       | String     | Authorization                             |
| getMacSha512()        | String     | Message authentication code.              |    
| getCompleteForm()		| String     | A complete Html form with method= "post" with submit button to include in your code. |
| getFormHtmlFields()   | Map<String, String>   | Map with html tags as keys with of Html form fields to include. |
 
```java
PaymentForm form = ...
    .getPaymentForm();
```
            
#### 1.5.3 PayPagePayment
*PayPage* with all available payments. You can also custom the *PayPage* by using one of the methods for *PayPagePayments*:
setPaymentMethod, includePaymentMethods, excludeCardPaymentMethods or excludeDirectPaymentMethods.
                
##### 1.5.3.1 Request
```java
PaymentForm form = WebPay.createOrder()
.setTestmode()
.addOrderRow(
    Item.orderRow()
        .setArticleNumber("1")
        .setQuantity(2)
        .setAmountExVat(100.00)
        .setDescription("Specification")
        .setName("Prod")
        .setUnit("st")
        .setVatPercent(25)
        .setDiscountPercent(0))   
	
	.setCountryCode(COUNTRYCODE.SE)
	.setCustomerReference("33")
	.setOrderDate("2012-12-12")
	.setCurrency("SEK")
	.usePayPage()
		.setReturnUrl("http://myurl.se")                   	//Required	
		.setMerchantIdBasedAuthorization(1200, "f78hv9")   	//Optional		
		.setCancelUrl("http://myurl.se")                   	//Optional
		.setPayPagePayment(LANGUAGECODE.SV)					//Optional, English is default. LANGUAGECODE see APPENDIX
		.getPaymentForm();
```               

###### 1.5.3.1.1 Exclude specific payment methods
Optional if you want to include specific payment methods for *PayPage*.
```java   
    .usePayPage()
        .setReturnUrl("http://myurl.se")                                            //Required
        .setCancelUrl("http://myurl.se")                                            //Optional
        .excludePaymentMethods(PaymentMethod.DBSEBSE, PaymentMethod.SVEAINVOICE_SE)	//Optional
        .getPaymentForm();
```
###### 1.5.3.1.2 Include specific payment methods
Optional if you want to include specific payment methods for *PayPage*.
```java   
    .usePayPage()
        .setReturnUrl("http://myurl.se")                                            //Required
        .includePaymentMethods(PaymentMethod.DBSEBSE, PaymentMethod.SVEAINVOICE_SE)	//Optional
        .getPaymentForm();
```

###### 1.5.3.1.3 Exclude Card payments
Optional if you want to exclude all card payment methods from *PayPage*.
```java
   .usePayPage()
        .setReturnUrl("http://myurl.se")                   //Required
        .excludeCardPaymentMethods()                       //Optional
        .getPaymentForm();
```

###### 1.5.3.1.4 Exclude Direct payments
Optional if you want to exclude all direct bank payments methods from *PayPage*.
```java  
.usePayPage()
    .setReturnUrl("http://myurl.se")                       //Required
    .excludeDirectPaymentMethods()                         //Optional
    .getPaymentForm();
```
##### 1.5.3.6 Return
Returns object type *PaymentForm*:
                
| Value                 | Returns    | Description                               |
|-----------------------|----------- |-------------------------------------------|
| getXmlMessageBase64() | String     | Payment information in XML-format, Base64 encoded.| 
| getXmlMessage()       | String     | Payment information in XML-format.        |
| getMerchantId()       | String     | Authorization                             |
| getSecretWord()       | String     | Authorization                             |
| getMacSha512()        | String     | Message authentication code.              |    
| getCompleteForm()		| String     | A complete Html form with method= "post" with submit button to include in your code. |
| getFormHtmlFields()   | Map<String, String>   | Map with html tags as keys with of Html form fields to include. |
 
```java
PaymentForm form = ...
        .getPaymentForm();
```

#### 1.5.4 PaymentMethod specified
Go direct to specified payment method without the step *PayPage*.

##### 1.5.1.1 Request
If Config/SveaConfig.php is not modified you can set your store authorization here.
```java
PaymentForm form = WebPay.createOrder()
.addOrderRow(Item.orderRow()
	.setArticleNumber("1")
	.setQuantity(2)
	.setAmountExVat(100.00)
	.setDescription("Specification")
	.setName("Prod")
	.setUnit("st")
	.setVatPercent(25)
	.setDiscountPercent(0))                  
		
    .setCountryCode(COUNTRYCODE.SE)
    .setClientOrderNumber("33")
    .setOrderDate("2012-12-12")
    .setCurrency("SEK")
        .usePaymentMethod(PaymentMethod.KORTCERT)             //Se APPENDIX for paymentmethods
            .setMerchantIdBasedAuthorization(1200, "f78hv9")  //Optional
            .setReturnUrl("http://myurl.se")                  //Required
            .setCancelUrl("http://myurl.se")                  //Optional
            .getPaymentForm();

```
##### 1.5.1.2 Return
The values of *xmlMessageBase64*, *merchantid* and *mac* are to be sent as xml to SveaWebPay.
Function getPaymentForm() returns Object type PaymentForm with accessible members:

| Value                 | Returns    | Description                               |
|-----------------------|----------- |-------------------------------------------|
| getXmlMessageBase64() | String     | Payment information in XML-format, Base64 encoded.| 
| getXmlMessage()       | String     | Payment information in XML-format.        |
| getMerchantId()       | String     | Authorization                             |
| getSecretWord()       | String     | Authorization                             |
| getMacSha512()        | String     | Message authentication code.              |    
| getCompleteForm()		| String     | A complete Html form with method= "post" with submit button to include in your code. |
| getFormHtmlFields()   | Map<String, String>   | Map with html tags as keys with of Html form fields to include. |
            
```
PaymentForm form = ...
    .getPaymentForm();

```

#### Synchronous solutions - Invoice and PaymentPlan
       
#### 1.5.4 InvoicePayment
Perform an invoice payment. This payment form will perform a synchronous payment and return a response.
Returns *CreateOrderResponse* object.
If Config/SveaConfig.php is not modified you can set your store authorization here.
```java
    CreateOrderResponse response = WebPay.createOrder()
      .setTestmode()
      .addOrderRow(Item.orderRow()
		.setArticleNumber("1")
        .setQuantity(2)
        .setAmountExVat(100.00)
        .setDescription("Specification")
        .setName("Prod")
        .setUnit("st")
        .setVatPercent(25)
        .setDiscountPercent(0))   
	
	.setCountryCode(COUNTRYCODE.SE)
	.setCustomerReference("33")
	.setOrderDate("2012-12-12")
	.setCurrency("SEK")
		.useInvoicePayment()
		.setPasswordBasedAuthorization("sverigetest", "sverigetest", 79021) //Optional
		.doRequest();
```
#### 1.5.5 PaymentPlanPayment
Perform *PaymentPlanPayment*. This payment form will perform a synchronous payment and return a response.
Returns a *CreateOrderResponse* object. Preceded by WebPay.getPaymentPlanParams().
If Config/SveaConfig.php is not modified you can set your store authorization here.
Param: Campaign code recieved from getPaymentPlanParams().
```java
CreateOrderResponse response = WebPay.createOrder()
.setTestmode()
.addOrderRow(Item.orderRow()
        .setArticleNumber(1)
        .setQuantity(2)
        .setAmountExVat(100.00)
        .setDescription("Specification")
        .setName("Prod")
        .setUnit("st")
        .setVatPercent(25)
        .setDiscountPercent(0))   
	
	.setCountryCode(COUNTRYCODE.SE)
	.setCustomerReference("33")
	.setOrderDate("2012-12-12")
	.setCurrency("SEK")
	.usePaymentPlanPayment("camp1", false)              //Parameter1: campaign code recieved from getPaymentPlanParams
														//Paremeter2: True if Automatic autogiro form will be sent with the first notification		
	   .setPasswordBasedAuthorization("sverigetest", "sverigetest", 79021) //Optional
	   .doRequest();
```
[<< To top](https://github.com/sveawebpay/java-integration/tree/develop#java-integration-package-api-for-sveawebpay)

## 2. getPaymentPlanParams   
Use this function to retrieve campaign codes for possible payment plan options. Use prior to create payment plan payment.
Returns *PaymentPlanParamsResponse* object.
If Config/SveaConfig.php is not modified you can set your store authorization here.

```java
    CreateOrderResponse response = WebPay.getPaymentPlanParams()
        .setTestmode()
        .setPasswordBasedAuthorization("sverigetest", "sverigetest", 79021) //Optional
        .doRequest();
```
[<< To top](https://github.com/sveawebpay/java-integration/tree/develop#java-integration-package-api-for-sveawebpay)

## 3. getAddresses 
Returns *getAddressesResponse* object with an *AddressSelector* for the associated addresses for a specific security number. 
Can be used when creating an order. Only applicable for SE, NO and DK.
If Config/Config.php is not modified you can set your store authorization here.

[<< To top](https://github.com/sveawebpay/java-integration/tree/develop#java-integration-package-api-for-sveawebpay)

### 3.1 Order type 
```java
    .setOrderTypeInvoice()         //Required if this is an invoice order
or
    .setOrderTypePaymentPlan()     //Required if this is a payment plan order
```
[<< To top](https://github.com/sveawebpay/java-integration/tree/develop#java-integration-package-api-for-sveawebpay)

### 3.2 Customer type 
```java
    .setIndividual("194605092222") //Required if this is an individual customer
or
    .setCompany("companyId")       //Required if this is a company customer
```
[<< To top](https://github.com/sveawebpay/java-integration/tree/develop#java-integration-package-api-for-sveawebpay)

### 3.3                                                                      	
```java
    GetAddressesResponse response = WebPay.getAddresses()
        .setTestmode()
        .setPasswordBasedAuthorization("sverigetest", "sverigetest", 79021) //Optional
        .setOrderTypeInvoice()                                              //See 3.1   
        .setCountryCode(COUNTRYCODE.SE)                                               //Required
        .setIndividual(194605092222)                                        //See 3.2   
        .doRequest();
```
[<< To top](https://github.com/sveawebpay/java-integration/tree/develop#java-integration-package-api-for-sveawebpay)

## 4. deliverOrder                                                           
Updates the status on a previous created order as delivered. Add rows that you want delivered. The rows will automatically be
matched with the rows that was sent when creating the order.
Only applicable for invoice and payment plan payments.
Returns *DeliverOrderResult* object.
If Config/SveaConfig.php is not modified you can set your store authorization here.

[<< To top](https://github.com/sveawebpay/java-integration/tree/develop#java-integration-package-api-for-sveawebpay)

### 4.1 Testmode                                                             
Set test mode while developing to make the calls to our test server.
Remove when you change to production mode.

Ex. 
```java
    .setTestmode()
```
[<< To top](https://github.com/sveawebpay/java-integration/tree/develop#java-integration-package-api-for-sveawebpay)

### 4.2 Specify order                                                        
Continue by adding values for products and other. You can add OrderRow, Fee and Discount. Chose the right Item object as parameter.
You can use the **add** functions with an Item object or an array of Item objects as parameters. 

```java
.addOrderRow(Item.orderRow() ...)

//or
orderRows = Item.orderRow()...; 
.addOrderRow(orderRows)
```

[<< To top](https://github.com/sveawebpay/java-integration/tree/develop#java-integration-package-api-for-sveawebpay)

#### 4.2.1 OrderRow
All products and other items. It is required to have a minimum of one row.
```java
  .addOrderRow(Item.orderRow()   
       .setQuantity(2)              	    //Required
       .setAmountExVat(100.00)  	        //Required
       .setVatPercent(25)    	            //Required
       .setArticleNumber("1")               //Optional
       .setDescription("Specification")	    //Optional    
       .setName("Prod")                 	//Optional
       .setUnit("st")                      	//Optional
       .setDiscountPercent(0)              	//Optional
   )
```

#### 4.2.2 ShippingFee
```java
.addFee(Item.shippingFee()
        .setAmountExVat(50)                //Required
        .setVatPercent(25)                 //Required
        .setShippingId("33")               //Optional
        .setName("shipping")               //Optional
        .setDescription("Specification")   //Optional        
        .setUnit("st")                     //Optional        
        .setDiscountPercent(0)
    )
```
#### 4.2.3 InvoiceFee
```java
.addFee(
    Item.invoiceFee()
        .setAmountExVat(50)                //Required
        .setVatPercent(25)                 //Required
        .setName("Svea fee")               //Optional
        .setDescription("Fee for invoice") //Optional       
        .setUnit("st")                     //Optional
        .setDiscountPercent(0)             //Optional
  )
```
[<< To top](https://github.com/sveawebpay/java-integration/tree/develop#java-integration-package-api-for-sveawebpay)

### 4.3 Other values  
Required is the order id received when creating the order. Required for invoice orders are *InvoiceDistributionType*. 
If invoice order is credit invoice use setCreditInvoice(invoiceId) and setNumberOfCreditDays(creditDaysAsInt)
```java
    .setOrderId(orderId)                  //Required. Received when creating order.
    .setNumberOfCreditDays(1)              //Use for Invoice orders.
    .setInvoiceDistributionType("Post")    //Use for Invoice orders. "Post" or "Email"
    .setCreditInvoice                      //Use for invoice orders, if this should be a credit invoice.
    .setNumberOfCreditDays(1)              //Use for invoice orders.
```

```java
DeliverOrderResponse response = WebPay.deliverOrder()
.setTestmode()
.addOrderRow(Item.orderRow()
        .setArticleNumber("1")
        .setQuantity(2)
        .setAmountExVat(100.00)
        .setDescription("Specification")
        .setName("Prod")
        .setUnit("st")
        .setVatPercent(25)
        .setDiscountPercent(0)
    )  
        .setOrderId(3434)
        .setInvoiceDistributionType("Post")
        .deliverInvoiceOrder()
            .setPasswordBasedAuthorization("sverigetest", "sverigetest", 79021) //Optional
            .doRequest();
```
[<< To top](https://github.com/sveawebpay/java-integration/tree/develop#java-integration-package-api-for-sveawebpay)

## 5. closeOrder                                                             
Use when you want to cancel an undelivered order. Valid only for invoice and payment plan orders. 
Required is the order id received when creating the order. If Config/SveaConfig.php is not modified you can set your store authorization here.

[<< To top](https://github.com/sveawebpay/java-integration/tree/develop#java-integration-package-api-for-sveawebpay)

### 5.1 Close by payment type                                                
```java
    .closeInvoiceOrder()
or
    .closePaymentPlanOrder()
```

```java
    CloseOrderResponse  =  WebPay.closeOrder()
        .setTestmode()
        .setOrderId(orderId)                                                  //Required, received when creating an order.
        .closeInvoiceOrder()
            .setPasswordBasedAuthorization("sverigetest", "sverigetest", 79021)//Optional
            .doRequest();
```
[<< To top](https://github.com/sveawebpay/java-integration/tree/develop#java-integration-package-api-for-sveawebpay)

## 6. Response handler                                                       
All synchronous responses are handled through *SveaResponse* and structured into objects.
Asynchronous responses recieved after sending the values *mac*, *merchantid* and *xmlMessageBase64* to
hosted solutions can also be processed through the *SveaResponse* class.

The response from server will be sent to the *returnUrl* with POST or GET. The response contains the parameters: 
*response*, *mac* and *merchantid*.
Class *SveaResponse* will return an object structured similar to the synchronous answer. 
Params: 
* The POST or GET message 
* Your *secret word*. //Optional if set in SveaConfig.php
```java
  SveaRespons respObject = nSveaResponse(responseXmlBase64, mac, secretWord); 
```
[<< To top](https://github.com/sveawebpay/java-integration/tree/develop#java-integration-package-api-for-sveawebpay)

## APPENDIX 

### PaymentMethods
Used in usePaymentMethod(paymentMethod) and in usePayPage(), 
.includePaymentMethods(Collection<PAYMENTMETHOD> paymentMethods), .includePaymentMethods(), .excludeCardPaymentMethods(Collection<PAYMENTMETHOD> paymentMethods), .excludeCardPaymentMethods(), .excludeDirectPaymentMethods(), .excludeCardPaymentMethods().

| Payment method                    | Description                                   |
|-----------------------------------|-----------------------------------------------|
| PAYMENTMETHOD.DBNORDEASE         | Direct bank payment, Nordea, Sweden.          | 
| PAYMENTMETHOD.DBSEBSE            | Direct bank payment, private, SEB, Sweden.    |
| PAYMENTMETHOD.DBSEBFTGSE         | Direct bank payment, company, SEB, Sweden.    |
| PAYMENTMETHOD.DBSHBSE            | Direct bank payment, Handelsbanken, Sweden.   |
| PAYMENTMETHOD.DBSWEDBANKSE       | Direct bank payment, Swedbank, Sweden.        |
| PAYMENTMETHOD.KORTCERT           | Card payments, Certitrade.                    |
| PAYMENTMETHOD.PAYPAL             | Paypal                                        |
| PAYMENTMETHOD.SKRILL             | Card payment with Dankort, Skrill.            |
| PAYMENTMETHOD.SVEAINVOICESE      | Invoice by PayPage in SE only.                |
| PAYMENTMETHOD.SVEASPLITSE        | PaymentPlan by PayPage in SE only.            |
| PAYMENTMETHOD.SVEAINVOICEEU_SE   | Invoice by PayPage in SE.                     |
| PAYMENTMETHOD.SVEAINVOICEEU_NO   | Invoice by PayPage in NO.                     |
| PAYMENTMETHOD.SVEAINVOICEEU_DK   | Invoice by PayPage in DK.                     |
| PAYMENTMETHOD.SVEAINVOICEEU_FI   | Invoice by PayPage in FI.                     |
| PAYMENTMETHOD.SVEAINVOICEEU_NL   | Invoice by PayPage in NL.                     |
| PAYMENTMETHOD.SVEAINVOICEEU_DE   | Invoice by PayPage in DE.                     |
| PAYMENTMETHOD.SVEASPLITEU_SE     | PaymentPlan by PayPage in SE.                 |
| PAYMENTMETHOD.SVEASPLITEU_NO     | PaymentPlan by PayPage in NO.                 |
| PAYMENTMETHOD.SVEASPLITEU_DK     | PaymentPlan by PayPage in DK.                 |
| PAYMENTMETHOD.SVEASPLITEU_FI     | PaymentPlan by PayPage in FI.                 |
| PAYMENTMETHOD.SVEASPLITEU_DE     | PaymentPlan by PayPage in DE.                 |
| PAYMENTMETHOD.SVEASPLITEU_NL     | PaymentPlan by PayPage in NL.                 |

[<< To top](https://github.com/sveawebpay/php-integration/tree/develop#php-integration-package-api-for-sveawebpay)

### CountryCode
ISO 3166-1 standard. Used in .setCountryCode(...).

| CountryCode						| Country					|
|-----------------------------------|---------------------------|
| COUNTRYCODE.DE					| Germany					|
| COUNTRYCODE.DK					| Denmark 					|
| COUNTRYCODE.FI					| Finland					|
| COUNTRYCODE.NL					| Netherlands				|
| COUNTRYCODE.NO					| Norway					|
| COUNTRYCODE.SE					| Sweden					|

[<< To top](https://github.com/sveawebpay/php-integration/tree/develop#php-integration-package-api-for-sveawebpay)

### LanguageCode
ISO 639-1 standard. Used in .setPayPageLanguage(...).

| LanguageCode						| Language name				|
|-----------------------------------|---------------------------|
| LANGUAGECODE.da					| Danish					|
| LANGUAGECODE.de					| German					|
| LANGUAGECODE.en					| English					|
| LANGUAGECODE.es					| Spanish					|
| LANGUAGECODE.fr					| French					|
| LANGUAGECODE.fi					| Finnish					|
| LANGUAGECODE.it					| Italian					|
| LANGUAGECODE.nl					| Dutch						|
| LANGUAGECODE.no					| Norwegian					|
| LANGUAGECODE.sv					| Swedish					|

[<< To top](https://github.com/sveawebpay/php-integration/tree/develop#php-integration-package-api-for-sveawebpay)

### Currency 
ISO 4217 standard. Used in .setCurrency(...).

| CurrencyCode						| Currency name				|
|-----------------------------------|---------------------------|
| CURRENCY.DKK						| Danish krone				|
| CURRENCY.EUR						| Euro						|
| CURRENCY.NOK						| Norwegian krone			|
| CURRENCY.SEK						| Swedish krona				|

[<< To top](https://github.com/sveawebpay/php-integration/tree/develop#php-integration-package-api-for-sveawebpay)

### Invoice Distribution Type 
Used in .setInvoiceDistributionType(...).

| DistributionType					| Description				|
|-----------------------------------|---------------------------|
| Post								| Invoice is sent by mail	|
| Email								| Invoice is sent by e-mail	|

[<< To top](https://github.com/sveawebpay/php-integration/tree/develop#php-integration-package-api-for-sveawebpay)