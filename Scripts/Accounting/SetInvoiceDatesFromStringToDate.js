var InvoiceDate = new Date(document.properties["rwa:InvoiceDateText"]);
document.properties["rwa:InvoiceDate"] = InvoiceDate;

var InvoiceDueDate = new Date(document.properties["rwa:InvoiceDueDateText"]);
document.properties["rwa:InvoiceDueDate"] = InvoiceDueDate;