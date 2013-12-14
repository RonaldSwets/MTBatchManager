MTBatchManager
==============

multi-threaded batch processing manager. With this library you can forget about spawning and managing threads. You
still need to do something though, you have to implement at least a Worker and a Manager, probably a WorkUnit and
maybe a Result. Here's a small and useless example for multi-threaded counting:

We must define a WorkUnit. The WorkUnit is a piece of work a single Worker thread must handle. This can be anything,
and we'll use this simple range object:
```java
public class WorkUnit {
  private int min;
  private int max;
  
  // getters and setters
}
```

Then we need a worker. A worker is run in it's own thread and must process one WorkUnit. Also, the worker may create
a result object. For this example, we'll use an Integer to keep track of how much we counted.
```java
public class Worker extends MTWorker<WorkUnit, Integer> {
  private int c;
  
  public void process(WorkUnit workUnit) {
    int min = workUnit.getMin();
    int max = workUnit.getMax();
    this.c = 0;
    
    for (int i = min; i < max; i++) {
      System.out.println("count: " + i);
      c++;
    }
  }
  
  public Integer getResult() {
    return this.c;
  }
}
```

Finally we need to implement the Manager. The only thing we need to manage here is the total amount of work to be
done, and create WorkUnits for our threads:
```java
public class Manager extends MTBatchManager<Worker, WorkUnit, Integer> {
  private static final int BATCH_SIZE = 10;
  
  private int max;
  private int currentBatchStart;
  private int counted;
  
  public Manager() {
    this.max = 100;
    this.currentBatchStart = 0;
    this.counted = 0;
  }
  
  // create a new work unit
  public WorkUnit getNextWorkUnit() {
    // returning null will signal the MTBatchManager there's no more work, now we only have to wait for the running
    // threads to be finished
    if (this.currentBatchStart > this.max) {
      return null;
    }
    
    int batchMin = this.currentBatchStart;
    int batchMax = batchMin + BATCH_SIZE;
    this.currentBatchStart = batchMax;
    
    WorkUnit workUnit = new WorkUnit();
    workUnit.setMin(batchMin);
    workUnit.setMax(batchMax);
    return workUnit;
  }
  
  // handle the result of a finished thread
  public void handleResult(Integer result) {
    this.counted += result;
  }
  
  // for test purposes
  public int getCounted() {
    return this.counted;
  }
}
```

Finally, we need a program to run the manager
```java
public class MTBatchTest {
  public static void main(String[] args) {
    Manager manager = new Manager();
    manager.run();  // this method is blocking and waits until there is no more work
    System.out.printn("counted: " + manager.getCounted());
  }
}
```
