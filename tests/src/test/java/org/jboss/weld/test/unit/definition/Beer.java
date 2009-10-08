package org.jboss.weld.test.unit.definition;

import javax.inject.Named;

public
@Named
class Beer
{
   private String name = "Chimay Grande Reserve (Blue)";
   
   private String style = "Belgium Strong Dark Ale";

   public Beer() {}
   
   public Beer(String name, String style)
   {
      this.name = name;
      this.style = style;
   }
   
   public String getName()
   {
      return name;
   }
   
   public String getStyle()
   {
      return style;
   }
}