# SimpleSection
SimpleSection was created to make course request and drop/add a breeze for Virginia Tech students.

Using SimpleSection, you can find classes and sections, sort them, and save them. You can also easily plan out your schedule and check professor reviews all from one place, making SimpleSection your one-stop shop for all things course registration.

Features:
- Search and sort class sections
- Create, save, and export a list of your classes
- Get notified if you've selected conflicting classes before making changes in HokieSPA
- Check section availability
- Compare professor reviews, access GPA histories, and make changes inside HokieSpa in-app.

Requirements:
- Windows Vista or greater, macOS/OSX, Linux
- Java 8u40 or greater. (Oracle JRE/JDK only. OpenJDK is unsupported.)
- A screen of at least 1200x720 is recommended, but not required.

You can get the latest version of Java [here](https://java.com/en/download/).

#Installing
If you are interested in giving it a shot, go to the releases tab up top to download.

On Windows: extract the file and double-click the .jar file to launch.

On OSX/Linux:
Extract, open a terminal, navigate to the folder with ``cd``, and run ``java -jar SimpleSection.jar``.

#Frequently Asked Questions
Is my password secure when logging in through SimpleSection?
- Yes, it is. SimpleSection does not store or send your data anywhere other than HokieSPA when logging in. It logs in exactly as you would, except through an headless web browser. If you want to read more about headless web browsers, check out the Selenium WebDriver project.

What methods of Two-Factor Authentication are supported?
- Push is the only supported method at the moment.

How do I sort classes?
- In the table, click on the title for each column to sort ascending or descending.

Can you add X feature or fix Y bug?
- Sure! Just file an issue against this project and it'll be looked at. Alternatively, if you want to do it yourself, submit a pull request.

#Contributing
Thanks for your interest. To get started, you'll need a working Efxclipse install, and import this repository as a project into eclipse from there. You'll also need maven if you want to add dependencies or package the project.

To build for distribution outside of Eclipse, use ``mvn jfx:jar``. A new JAR file will be generated from the info in ``pom.xml``. You'll need to bundle the ``res`` folder together with the jar. See JavaFX-Maven-Plugin for more details.
