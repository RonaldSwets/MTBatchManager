package er.commons.mtbatch;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Multi-Thread Batch Manager
 * 
 * @author Eric
 */
public abstract class MTBatchManager<W extends MTWorker<U, R>, U, R> {
   private static final int DEFAULT_MAX_THREADS = 8;
   
   private Class<W> workerClass;
   private List<W> workerThreads;
   
   /**
    * Constructor
    * 
    * @param workerClass
    */
   public MTBatchManager(Class<W> workerClass) {
      this.workerClass = workerClass;
      this.workerThreads = new ArrayList<W>();
   }
   
   /**
    * Run. This method blocks until all work is done (e.g. the getNextWorkUnit()
    * method returns null and all worker threads are done).
    */
   public void run() {
      this.initialize();
      boolean done = false;
      
      while (!done || (done && this.workerThreads.size() > 0)) {
         // keep adding threads until we are at max threads or there are no work
         // units anymore.
         if (this.workerThreads.size() < this.getMaxThreads() && !done) {
            U unit = this.getNextWorkUnit();
            if (unit == null) {
               done = true;
            } else {
               try {
                  // create the worker
                  W worker = this.workerClass.newInstance();
                  worker.setWorkUnit(unit);
                  this.workerThreads.add(worker);
                  
                  // create and start the thread
                  Thread workerThread = new Thread(worker);
                  workerThread.start();
               } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | SecurityException e) {
                  System.err.println("MTBatchManager [" + this.getClass().getName() + "] was unable to create a worker: " + e.getMessage());
               }
            }
         }
         
         // check for threads which are done or cancelled
         Iterator<W> workerThreadIterator = this.workerThreads.iterator();
         while (workerThreadIterator.hasNext()) {
            W worker = workerThreadIterator.next();
            if (worker.isDone()) {
               this.handleResult(worker.getResult());
               workerThreadIterator.remove();
            }
         }
         
         // play nice
         try {
            Thread.sleep(50);
         } catch (InterruptedException e) {
            System.err.println("MTBatchManager [" + this.getClass().getName() + "] was interrupted while sleeping");
         }
      }
      
      this.done();
   }
   
   /**
    * Implementations may decide to do something with the result returned from
    * each batch.
    * 
    * @param result
    */
   protected void handleResult(R result) {
      // empty by default
   }
   
   /**
    * Implementations may override this method to do initialization. This method
    * is called as first statement of the run() method.
    */
   protected void initialize() {
      // empty by default
   }
   
   /**
    * Implementations may override this method to do something when all work is
    * done. This method is called as last statement in the run() method.
    */
   protected void done() {
      // empty by default
   }
   
   /**
    * Return the maximum number of worker threads running. This defaults to the
    * value of DEFAULT_MAX_THREADS.
    * 
    * @return int
    */
   protected int getMaxThreads() {
      return DEFAULT_MAX_THREADS;
   }
   
   /**
    * Implementations must implement this method to return a new unit of work
    * which will be done by a single thread.
    * 
    * @return U
    */
   protected abstract U getNextWorkUnit();
}
