/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jams.workspace.stores;

import de.odysseus.el.util.SimpleContext;
import jams.data.Attribute;
import jams.data.DefaultDataFactory;
import jams.data.JAMSData;
import jams.data.JAMSInteger;
import jams.model.Context;
import javax.smartcardio.ATR;

/**
 *
 * @author christian
 */
public class FilterFunctions {
                                            
    public static Long toLong(String in){
        return Long.parseLong(in);
    }
    
    public static int attrToInt (Attribute.Integer a){
        return a.getValue();
    }
    
    public static Object getValue (JAMSData a){
        if (a instanceof Attribute.Boolean){
            return ((Attribute.Boolean)a).getValue();
        }else if (a instanceof Attribute.BooleanArray){
            return ((Attribute.BooleanArray)a).getValue();
        }else if (a instanceof Attribute.Integer){
            return ((Attribute.Integer)a).getValue();
        }else if (a instanceof Attribute.IntegerArray){
            return ((Attribute.IntegerArray)a).getValue();
        }else if (a instanceof Attribute.Long){
            return ((Attribute.Long)a).getValue();
        }else if (a instanceof Attribute.LongArray){
            return ((Attribute.LongArray)a).getValue();
        }else if (a instanceof Attribute.Double){
            return ((Attribute.Double)a).getValue();
        }else if (a instanceof Attribute.DoubleArray){
            return ((Attribute.DoubleArray)a).getValue();
        }else if (a instanceof Attribute.Float){
            return ((Attribute.Float)a).getValue();
        }else if (a instanceof Attribute.FloatArray){
            return ((Attribute.FloatArray)a).getValue();
        }else if (a instanceof Attribute.String){
            return ((Attribute.String)a).getValue();
        }else if (a instanceof Attribute.StringArray){
            return ((Attribute.StringArray)a).getValue();
        }else if (a instanceof Attribute.Calendar){
            return ((Attribute.Calendar)a).getValue();
        }
        
        return null;
    }
                
    public static Attribute.Calendar toDate(String in){
        Attribute.Calendar calendar = DefaultDataFactory.getDataFactory().createCalendar();
        calendar.setValue(in);
        return calendar;
    }
    
    public static Attribute.TimeInterval toTimeInterval(String in){
        Attribute.TimeInterval interval = DefaultDataFactory.getDataFactory().createTimeInterval();
        interval.setValue(in);
        return interval;
    }
    
    public static Attribute.Calendar start(Attribute.TimeInterval in){
        return in.getStart();
    }
    
    public static Attribute.Calendar end(Attribute.TimeInterval in){
        return in.getEnd();
    }
    
    public static int day(Attribute.Calendar c){        
        return c.get(Attribute.Calendar.DAY_OF_MONTH);
    }
    
    public static int month(Attribute.Calendar c){        
        return c.get(Attribute.Calendar.DAY_OF_MONTH);
    }
    
    public static int year(Attribute.Calendar c){        
        return c.get(Attribute.Calendar.DAY_OF_MONTH);
    }
    
    public static int lastDayInMonth(Attribute.Calendar c){        
        return c.getActualMaximum(Attribute.Calendar.DAY_OF_MONTH); 
    }
    
    public static boolean isLastDayInMonth(Attribute.Calendar c){        
        return c.getActualMaximum(Attribute.Calendar.DAY_OF_MONTH) == c.get(Attribute.Calendar.DAY_OF_MONTH); 
    }
    
    public static JAMSData getAttribute(Context context, String name){        
        return context.getAttributeMap().get(name);
    }
    
    public static int longCompare(long a, long b){
        if (a<b)
            return -1;
        if (a>b)
            return 1;
        return 0;
    }
    
    public static int dateCompare(Attribute.Calendar c1, Attribute.Calendar c2, int accuracy){        
        Attribute.Calendar a = c1.clone();
        Attribute.Calendar b = c2.clone();
        a.removeUnsignificantComponents(accuracy);
        b.removeUnsignificantComponents(accuracy);
        return longCompare(a.getTimeInMillis(), b.getTimeInMillis());        
    }
    
    
           
    public static SimpleContext getContext(){
        SimpleContext context = new SimpleContext();
         
        try{                        
            context.setFunction("", "day", FilterFunctions.class.getMethod("day", Attribute.Calendar.class));
            context.setFunction("", "month", FilterFunctions.class.getMethod("month", Attribute.Calendar.class));
            context.setFunction("", "year", FilterFunctions.class.getMethod("year", Attribute.Calendar.class));
            context.setFunction("", "daysInMonth", FilterFunctions.class.getMethod("lastDayInMonth", Attribute.Calendar.class));           
            context.setFunction("", "isLastDayInMonth", FilterFunctions.class.getMethod("isLastDayInMonth", Attribute.Calendar.class));           
            context.setFunction("", "dateCompare", FilterFunctions.class.getMethod("dateCompare", Attribute.Calendar.class, Attribute.Calendar.class, int.class));   
            
            context.setFunction("", "toDate", FilterFunctions.class.getMethod("toDate", String.class)); 
            context.setFunction("", "toTimeInterval", FilterFunctions.class.getMethod("toTimeInterval", String.class)); 
            context.setFunction("interval", "start", FilterFunctions.class.getMethod("start", Attribute.TimeInterval.class)); 
            context.setFunction("interval", "end", FilterFunctions.class.getMethod("end", Attribute.TimeInterval.class)); 
            context.setFunction("", "toLong", FilterFunctions.class.getMethod("toLong", String.class));   
            context.setFunction("", "attrToInt", FilterFunctions.class.getMethod("attrToInt", Attribute.Integer.class)); 
            context.setFunction("", "getValue", FilterFunctions.class.getMethod("getValue", JAMSData.class)); 
            context.setFunction("", "getAttribute", FilterFunctions.class.getMethod("getAttribute", Context.class, String.class));     
            
        }catch(NoSuchMethodException nsme){
            nsme.printStackTrace();
        }
        return context;
    }
}
