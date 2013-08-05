package net.minecraft.launcher.process;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import net.minecraft.launcher.OperatingSystem;
import net.minecraft.launcher.process.JavaProcess;

public class JavaProcessLauncher {

   private final String jvmPath;
   private final List<String> commands;
   private File directory;


   public JavaProcessLauncher(String jvmPath, String ... commands) {
      if(jvmPath == null) {
         jvmPath = OperatingSystem.getCurrentPlatform().getJavaDir();
      }

      this.jvmPath = jvmPath;
      this.commands = new ArrayList(commands.length);
      this.addCommands(commands);
   }

   public JavaProcess start() throws IOException {
      List full = this.getFullCommands();
      return new JavaProcess(full, (new ProcessBuilder(full)).directory(this.directory).redirectErrorStream(true).start());
   }

   public List<String> getFullCommands() {
      ArrayList result = new ArrayList(this.commands);
      result.add(0, this.getJavaPath());
      return result;
   }

   public List<String> getCommands() {
      return this.commands;
   }

   public void addCommands(String ... commands) {
      this.commands.addAll(Arrays.asList(commands));
   }

   public void addSplitCommands(String commands) {
      this.addCommands(commands.split(" "));
   }

   public JavaProcessLauncher directory(File directory) {
      this.directory = directory;
      return this;
   }

   public File getDirectory() {
      return this.directory;
   }

   protected String getJavaPath() {
      return this.jvmPath;
   }

   public String toString() {
      return "JavaProcessLauncher[commands=" + this.commands + ", java=" + this.jvmPath + "]";
   }
}
