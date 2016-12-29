This is more like the classic version of SparkJava - for most use cases, it is perfectly compatible, meaning all 
the example usages from the upstream project's [README](https://github.com/perwendel/spark) will work as is.
 
What is different though interoperation with these former classes with many static methods:
 
* CustomErrorPages
* RouteOverview
* ExceptionMapper
* MimeType
 
With the new version, methods have been added to Spark, as accessors to their new instance-centric reality. These 
methods specifically:

* customErrorPages()
* routeOverview()
* exceptionMapper()
* mimeTypes()

**Importantly**, SparkFilter has been deleted, so you're not going to be able to package a WAR file and deploy it to a 
regular Java HTTP container. It could be added back, I guess (Pull Requests accepted).

