
var parts =document.properties["rwa:InvoiceDateText"].split('/');

var InvoiceDate = new Date(parts[2],parts[0]-1,parts[1]);

document.properties["rwa:InvoiceDate"] = InvoiceDate;