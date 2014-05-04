Truss Webapp
------------

A webapp for the PAVE Statics class.

### Build

- Install SBT: http://www.scala-sbt.org/

- In the root directory of the repo, use the command `sbt compile` to compile and `sbt run` to start the server.
  Alternatively, use the command `sbt` to start the SBT shell, then use the commands `compile`
  and `run` there.

### Importing into IntelliJ IDEA

If you are not using the best IDE in the world, maybe you should do that.
Do "Import Project", and then in the directory selection window, navigate to
this directory and select the `build.sbt` file. Click "import", wait for
a minute, and IDEA should have everything setup. It's that easy. You may have
to install the SBT plugin and the Scala plugin first.
