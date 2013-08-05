package net.minecraft.launcher.updater;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;

public class LowerCaseEnumTypeAdapterFactory implements TypeAdapterFactory {

   public <T extends Object> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
      Class rawType = type.getRawType();
      if(!rawType.isEnum()) {
         return null;
      } else {
         final HashMap lowercaseToConstant = new HashMap();
         Object[] arr$ = rawType.getEnumConstants();
         int len$ = arr$.length;

         for(int i$ = 0; i$ < len$; ++i$) {
            Object constant = arr$[i$];
            lowercaseToConstant.put(this.toLowercase(constant), constant);
         }

         return new TypeAdapter<T>() {
            public void write(JsonWriter out, T value) throws IOException {
               if(value == null) {
                  out.nullValue();
               } else {
                  out.value(LowerCaseEnumTypeAdapterFactory.this.toLowercase(value));
               }

            }
            public T read(JsonReader reader) throws IOException {
               if(reader.peek() == JsonToken.NULL) {
                  reader.nextNull();
                  return null;
               } else {
                  return (T) lowercaseToConstant.get(reader.nextString());
               }
            }
         };
      }
   }

   private String toLowercase(Object o) {
      return o.toString().toLowerCase(Locale.US);
   }
}
