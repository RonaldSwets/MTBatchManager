package er.commons.mtbatch;

/**
 * This class will run in it's own thread and should process the given work unit
 * U.
 * 
 * @author Eric
 */
public abstract class MTWorker<U, R> implements Runnable {
   protected U workUnit;
   private boolean done;
   
   /**
    * Implementations must implement this method to process the given workunit.
    * 
    * @param workUnit
    */
   protected abstract void process(U workUnit);
   
   /**
    * Set the work unit
    * 
    * @param workUnit
    */
   public final void setWorkUnit(U workUnit) {
      this.workUnit = workUnit;
   }
   
   /**
    * Implementations must return the result here. The default implementation
    * returns null.
    * 
    * @return R
    */
   public R getResult() {
      return null;
   }
   
   /**
    * Run the worker thread
    * 
    * @see java.lang.Runnable#run()
    */
   @Override()
   public void run() {
      this.done = false;
      this.process(this.workUnit);
      this.done = true;
   }
   
   /**
    * Test to see if this worker is done.
    * 
    * @return boolean
    */
   public boolean isDone() {
      return this.done;
   }
}
