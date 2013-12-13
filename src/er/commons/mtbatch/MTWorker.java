package er.commons.mtbatch;

/**
 * This class will run in it's own thread and should process the given work unit
 * U.
 * 
 * @author Eric
 */
public abstract class MTWorker<U, R> extends Thread {
   protected U workUnit;
   
   /**
    * Constructor
    * 
    * @param workUnit
    */
   public MTWorker(U workUnit) {
      this.workUnit = workUnit;
   }
   
   /**
    * Implementations must implement this method to process the given workunit.
    * 
    * @param workUnit
    */
   protected abstract void process(U workUnit);
   
   /**
    * Implementations must return the result here.
    * 
    * @return R
    */
   public abstract R getResult();
   
   /**
    * Run this work unit
    * 
    * @see java.lang.Thread#run()
    */
   @Override()
   public void run() {
      this.process(this.workUnit);
   }
}
