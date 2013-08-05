package net.minecraft.launcher.updater.download;

import net.minecraft.launcher.updater.download.DownloadJob;

public class ProgressContainer {

   private long total;
   private long current;
   private DownloadJob job;


   public DownloadJob getJob() {
      return this.job;
   }

   public void setJob(DownloadJob job) {
      this.job = job;
      if(job != null) {
         job.updateProgress();
      }

   }

   public long getTotal() {
      return this.total;
   }

   public void setTotal(long total) {
      this.total = total;
      if(this.job != null) {
         this.job.updateProgress();
      }

   }

   public long getCurrent() {
      return this.current;
   }

   public void setCurrent(long current) {
      this.current = current;
      if(current > this.total) {
         this.total = current;
      }

      if(this.job != null) {
         this.job.updateProgress();
      }

   }

   public void addProgress(long amount) {
      this.setCurrent(this.getCurrent() + amount);
   }

   public float getProgress() {
      return this.total == 0L?0.0F:(float)this.current / (float)this.total;
   }
}
