
for each (var doc in document.children) {
   doc.properties["rwa:PaymentStatus"] = "Complete";
    doc.properties["rwa:AuditStatus"] = "Complete";
   doc.save();
}