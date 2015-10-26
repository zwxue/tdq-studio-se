#!/bin/sh
# ============================================================================
#
# Copyright (C) 2006-2015 Talend Inc. - www.talend.com
#
# This source code is available under agreement available at
# %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
#
# You should have received a copy of the agreement
# along with this program; if not, write to Talend SA
# 9 rue Pages 92150 Suresnes, France
#
# ============================================================================
#
# @author sizhaoliu 2015-10-26
#
# This script is used to deploy plugins to Talend Update Nexus Repository.
# Usage:
# 1. config the settings.xml file in your M2_REPO folder.
#    put the following lines in the servers section:
#	<servers>
#		<server>
#			<id>talend-update</id>
#			<username>your_username</username>
#			<password>your_password</password>
#		</server>
#		...
#	</servers>
# 2. copy this file to the folder where the plugins to deploy are exported.
# 3. run the script form shell. (Not supported on windows unless a shell environment
#	 is installed, such as Cygwin, MinGW, Git Bash)

list="net.sourceforge.sqlexplorer net.sourceforge.sqlexplorer.nl \
 org.talend.dataprofiler.top.chart org.talend.dataprofiler.top.chart.nl"
version="6.1.0"
repo_id="talend-update"
repo_url="https://talend-update.talend.com/nexus/content/repositories/libraries"

for element in $list
do
	echo "----------------------------------------------------"
	echo "|     " ${element}_${version} "     |"
	echo "----------------------------------------------------"
	mvn deploy:deploy-file -DpomFile=${element}_${version}.pom \
      -Dfile=${element}_${version}.jar \
      -DrepositoryId=$repo_id \
      -Durl=$repo_url
done
