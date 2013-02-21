package se.sveaekonomi.webpay.integration.response.webservice;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import se.sveaekonomi.webpay.integration.response.Response;



public class DeliverOrderResponse extends Response {
    
    private double amount;
    private String orderType; //"Invoice" or "PaymentPlan"
    private int invoiceId;
    private String dueDate;
    private String invoiceDate;
    private String invoiceDistributionType;
    private int contractNumber;  
    
      
    public DeliverOrderResponse(NodeList soapResponse) {
        super();
        this.setValues(soapResponse);
    }

    public void setValues(NodeList soapResponse) {
        try {
            int size = soapResponse.getLength();
            
            for (int i = 0; i < size; i++)
            {
                Element node = (Element) soapResponse.item(i);
                this.setOrderAccepted(Boolean.parseBoolean(getTagValue(node, "Accepted")));
                this.setResultCode(Long.parseLong(getTagValue(node, "ResultCode")));
                String errorMsg = getTagValue(node, "ErrorMessage");
                
                if(errorMsg != null)
                    this.setErrorMessage(errorMsg);
                else {                    
                    this.setAmount(Double.parseDouble(getTagValue(node, "Amount")));
                    this.orderType = getTagValue(node, "OrderType");
                    if(this.orderType=="Invoice") {
                        // Set child nodes from InvoiceResultDetails
                        setChildNodeValue(node, "InvoiceId");
                        setChildNodeValue(node, "DueDate");
                        setChildNodeValue(node, "InvoiceDate");
                        setChildNodeValue(node, "InvoiceDistributionType");
                    }
                    else {
                        setChildNodeValue(node, "ContractNumber");
                    }
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }         
    }
    
    private String getTagValue(Element elementNode, String tagName) {
        NodeList nodeList = elementNode.getElementsByTagName(tagName);
        Element element = (Element) nodeList.item(0);
        if (element != null && element.hasChildNodes()) {
            NodeList textList = element.getChildNodes();
            return ((Node) textList.item(0)).getNodeValue().trim();
        }
        return null;
    }
           
    private void setChildNodeValue(Node n, String tagName) {
        String tagValue = "";
        
        if (n.hasChildNodes()) {
            NodeList nl = n.getChildNodes();
            int length = nl.getLength();
            for (int j = 0; j < length; j++) {
                
                Node childNode = nl.item(j);
                String nodeName = childNode.getNodeName();
                
                if (nodeName.equals(tagName)) {
                    tagValue = getTagValue((Element) n, tagName);
                    if (tagValue != null)
                        this.setValue(tagName, tagValue);
                }
                setChildNodeValue(childNode, tagName);
            }
        }
    }

    private void setValue(String tagName, String tagValue) {
        if(tagName.equals("InvoiceId"))
            this.setInvoiceId(Integer.valueOf(tagValue));
        else if(tagName.equals("DueDate"))
            this.setDueDate(tagValue);
        else if(tagName.equals("InvoiceDate"))
            this.setInvoiceDate(tagValue);
        else if(tagName.equals("InvoiceDistributionType"))
            this.setInvoiceDistributionType(tagValue);
        else if(tagName.equals("ContractNumber"))
            this.setContractNumber(Integer.valueOf(tagValue));
    }

    public String getOrderType() {
        return orderType;
    }
    
    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }
    
    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public int getInvoiceId() {
        return invoiceId;
    }

    public void setInvoiceId(int invoiceId) {
        this.invoiceId = invoiceId;
    }

    public String getDueDate() {
        return dueDate;
    }

    public void setDueDate(String dueDate) {
        this.dueDate = dueDate;
    }

    public String getInvoiceDate() {
        return invoiceDate;
    }

    public void setInvoiceDate(String invoiceDate) {
        this.invoiceDate = invoiceDate;
    }

    public String getInvoiceDistributionType() {
        return invoiceDistributionType;
    }

    public void setInvoiceDistributionType(String invoiceDistributionType) {
        this.invoiceDistributionType = invoiceDistributionType;
    }

    public int getContractNumber() {
        return contractNumber;
    }

    public void setContractNumber(int contractNumber) {
        this.contractNumber = contractNumber;
    }
}