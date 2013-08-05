package net.minecraft.launcher.updater;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import net.minecraft.launcher.versions.ReleaseType;

public class VersionFilter {

   private final Set<ReleaseType> types = new HashSet();
   private int maxCount = 5;


   public VersionFilter() {
      Collections.addAll(this.types, ReleaseType.values());
   }

   public Set<ReleaseType> getTypes() {
      return this.types;
   }

   public VersionFilter onlyForTypes(ReleaseType ... types) {
      this.types.clear();
      this.includeTypes(types);
      return this;
   }

   public VersionFilter includeTypes(ReleaseType ... types) {
      if(types != null) {
         Collections.addAll(this.types, types);
      }

      return this;
   }

   public VersionFilter excludeTypes(ReleaseType ... types) {
      if(types != null) {
         ReleaseType[] arr$ = types;
         int len$ = types.length;

         for(int i$ = 0; i$ < len$; ++i$) {
            ReleaseType type = arr$[i$];
            this.types.remove(type);
         }
      }

      return this;
   }

   public int getMaxCount() {
      return this.maxCount;
   }

   public VersionFilter setMaxCount(int maxCount) {
      this.maxCount = maxCount;
      return this;
   }
}
