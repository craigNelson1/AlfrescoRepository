var headerLogo = widgetUtils.findObject(model.jsonModel, "id",
		"HEADER_LOGO");
if (headerLogo != null) {
	
	headerLogo.name="alfresco/redwing/Logo";
	headerLogo.config=
    {
       logoClasses: "alfresco-logo-only",
       currentTheme: theme,
    };

}
