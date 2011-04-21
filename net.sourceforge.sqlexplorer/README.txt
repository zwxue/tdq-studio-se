

Eclipse SQL Explorer v3.5.0
===========================

Upgrading from Previous Versions (Plugin and RCP)
=================================================
When you start Eclipse for the first time after installing a new version, you MUST use the 
-clean command line argument to eclipse; this resets cached information in Eclipse that can
cause problems.


Upgrading from Previous Releases (Plugin Only)
==============================================
Before upgrading (including from beta versions) you must remove previous installations
of SQLExplorer; to do this, locate your Eclipse directory and delete:

	Folders to delete
	-----------------
	features/net.sourceforge.sqlexplorer_3.5.0.*
	plugins/net.sourceforge.sqlexplorer.help_3.5.0.*
	plugins/net.sourceforge.sqlexplorer_3.5.0.*
	
	Files to delete
	---------------
	plugins/net.sourceforge.sqlexplorer.db2_3.5.0.*.jar
	plugins/net.sourceforge.sqlexplorer.mysql_3.5.0.*.jar
	plugins/net.sourceforge.sqlexplorer.oracle_3.5.0.*.jar
	plugins/net.sourceforge.sqlexplorer.postgresql_3.5.0.*.jar


JRE Version (RCP Users)
=======================
SQLExplorer 3.5.0 requires that the JRE is at least v5.0 (aka JRE 1.5); if you are still using
JRE 1.4 this is not necessarily - simply download the "SQLExplorer RCP (inc JRE)" version.


JRE Version (Plugin Users)
==========================
SQLExplorer 3.5.0 requires that the JRE is at least v5.0 (aka JRE 1.5); if you are still using
JRE 1.4 this is not necessarily a problem, even if the software you develop using Eclipse has
to target 1.4 because Eclipse can run using a /different/ JRE to the one used to compile, run,
and debug your application(s).

There are two ways to do this, which one you choose depends on whether you want to use your
new JRE5.0 for more than just running Eclipse.

Option A - JRE5.0 is *only* used to run Eclipse
-----------------------------------------------
Install the JRE into a subdirectory of your Eclipse installation directory called "jre"; note 
that the default installation path includes version numbers etc - the folder must be called 
simply "jre".  To be clear, you should have a directory layout like this:

	c:\eclipse\
		eclipse.exe
		jre\
			bin\
				javaw.exe

Every time Eclipse starts it looks for a "jre" folder and will use that by default.  You do
NOT need to have the JRE on the system PATH or be referenced from anywhere so you can be
reasonably assured that the JRE you develop against (eg v1.4) has not been replaced.


Option B - JRE5.0 is used for Eclipse and other programs, just not always
-------------------------------------------------------------------------
Install the JRE in the default place and use the -vm parameter when starting Eclipse; eg,
under Windows, modify your shortcut like so:

	c:\eclipse\eclipse.exe -vm "c:\Program Files\Java\jre_1.5.0_13\bin\javaw.exe"
	
NOTE: This is actually how you are recommended to start Eclipse because if your OS path
is changed you might get unexpected results if you find that your JRE does too.



2010-06-01 klliu Data explorer perspective display error 
---------------------------------------------------------------------------------------
In this case you must check "messages.properties" in "net.sourceforge.sqlexplorer\src",and 
you can find the about oracle's bundle key.

NOTE: Add the content of "text.properties" to "messages.properties", the path of  "text.properties" like so:
net.sourceforge.sqlexplorer.oracle_3.5.0.jar\net\sourceforge\sqlexplorer\oracle\text.properties


2010-7-2 qiongli bug 13093:the function in Data Explorer for view the information of data structure not work well 
---------------------------------------------------------------------------------------------------
The method in class of "net.sourceforge.sqlexplorer.plugin.views.DatabaseStructureView" has been modified:
1.Delete the condition : 'if(_tabFolder == null)'
2.Add line 379 to 388: select correct item as active item.

2011-4-21qiongli bug 20205: the "Save Editor as" button can not create file under "Sourse file" 
---------------------------------------------------------------------------------------------------
1.Add a extension named 'saveAs'
2.Add Pacake 'service',contain:2 interface (ISaveAsService,IService) and a class GlobalServiceRegister.java.
3.Modify method createIFile of class SQLTextEditor.we should create propery file and item file by model.

