/*
 * Copyright (C) 2002-2004 Andrea Mazzolini
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

package net.sourceforge.sqlexplorer.dbstructure.actions;

import java.sql.SQLException;
import java.util.ArrayList;

import net.sourceforge.sqlexplorer.Messages;
import net.sourceforge.sqlexplorer.dbstructure.nodes.TableNode;
import net.sourceforge.sqlexplorer.plugin.SQLExplorerPlugin;
import net.sourceforge.sqlexplorer.plugin.editors.SQLEditor;
import net.sourceforge.sqlexplorer.plugin.editors.SQLEditorInput;
import net.sourceforge.sqlexplorer.util.ImageUtil;
import net.sourceforge.squirrel_sql.fw.sql.ITableInfo;
import net.sourceforge.squirrel_sql.fw.sql.PrimaryKeyInfo;
import net.sourceforge.squirrel_sql.fw.sql.SQLDatabaseMetaData;
import net.sourceforge.squirrel_sql.fw.sql.TableColumnInfo;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;

/**
 * Create table script for the selected node.
 * 
 * @modified Davy Vanherbergen
 * 
 */
public class CreateTableScriptAction extends AbstractDBTreeContextAction {

    private static final ImageDescriptor _image = ImageUtil.getDescriptor("Images.TableIcon"); //$NON-NLS-1$


    /**
     * Custom image for refresh action
     * 
     * @see org.eclipse.jface.action.IAction#getImageDescriptor()
     */
    public ImageDescriptor getImageDescriptor() {
        return _image;
    }


    /**
     * Set the text for the menu entry.
     * 
     * @see org.eclipse.jface.action.IAction#getText()
     */
    public String getText() {
        return Messages.getString("DatabaseStructureView.Actions.CreateTableScript"); //$NON-NLS-1$
    }


    /**
     * Create table script for selected node.
     * 
     * @see org.eclipse.jface.action.IAction#run()
     */
    public void run() {

        TableNode tableNode = (TableNode) _selectedNodes[0];
        ITableInfo info = tableNode.getTableInfo();

        StringBuffer buf = new StringBuffer(4 * 1024);
        String sep = System.getProperty("line.separator"); //$NON-NLS-1$

        try {
            SQLDatabaseMetaData metaData = tableNode.getSession().getMetaData();

            ArrayList<String> pks = new ArrayList<String>();
            PrimaryKeyInfo[] pksInfo = metaData.getPrimaryKey(info);
            for (PrimaryKeyInfo pkInfo : pksInfo)
            	pks.add(pkInfo.getColumnName());

            TableColumnInfo[] columnsInfo = metaData.getColumnInfo(info);
            String tableName = _selectedNodes[0].getQualifiedName();
            buf.append("CREATE TABLE "); //$NON-NLS-1$
            buf.append(tableName);
            buf.append("("); //$NON-NLS-1$

            for (TableColumnInfo col : columnsInfo) {
//                String columnName = resultSet.getString(4);
//                String typeName = resultSet.getString(6);
//                String columnSize = resultSet.getString(7);
//                String decimalDigits = resultSet.getString(9);
//                String defaultValue = resultSet.getString(13);
                boolean notNull = "NO".equalsIgnoreCase(col.isNullable());  //$NON-NLS-1$
                String sLower = col.getColumnName().toLowerCase();
                buf.append(sep);
                buf.append(col.getColumnName() + " "); //$NON-NLS-1$

                buf.append(col.getTypeName());

                boolean bNumeric = false;
                if (sLower.equals("numeric") || sLower.equals("number") || sLower.equals("decimal")) //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                    bNumeric = true;

                if (sLower.indexOf("char") != -1 || sLower.indexOf("int") != -1) { //$NON-NLS-1$ //$NON-NLS-2$
                    buf.append("("); //$NON-NLS-1$
                    buf.append(col.getColumnSize());
                    buf.append(")"); //$NON-NLS-1$
                    
                } else if (bNumeric) {
                    buf.append("("); //$NON-NLS-1$
                    buf.append(col.getColumnSize());
                    if (col.getDecimalDigits() > 0)
                        buf.append(col.getDecimalDigits());
                    buf.append(")"); //$NON-NLS-1$
                }
                
                if (pks.size() == 1 && pks.get(0).equals(col.getColumnName())) {
                    buf.append(" PRIMARY KEY"); //$NON-NLS-1$
                }

                String defaultValue = col.getDefaultValue();
                if (defaultValue != null && !defaultValue.equals("")) { //$NON-NLS-1$
                    buf.append(" default "); //$NON-NLS-1$
                    boolean isSystemValue = bNumeric; 
                    
                    if (defaultValue.equalsIgnoreCase("CURRENT_TIMESTAMP")) { //$NON-NLS-1$
                        isSystemValue = true;
                    }
                    
                    if (!isSystemValue)
                        buf.append("'"); //$NON-NLS-1$
                    buf.append(defaultValue);
                    if (!isSystemValue)
                        buf.append("'"); //$NON-NLS-1$

                }

                if (notNull) {
                    buf.append(" not null"); //$NON-NLS-1$
                }
                buf.append(","); //$NON-NLS-1$
            }
            buf.deleteCharAt(buf.length() - 1);
            buf.append(")" + sep); //$NON-NLS-1$

            SQLEditorInput input = new SQLEditorInput("SQL Editor (" + SQLExplorerPlugin.getDefault().getEditorSerialNo() + ").sql"); //$NON-NLS-1$ //$NON-NLS-2$
            input.setUser(_selectedNodes[0].getSession().getUser());
            IWorkbenchPage page = SQLExplorerPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage();

            SQLEditor editorPart = (SQLEditor) page.openEditor((IEditorInput) input, "net.sourceforge.sqlexplorer.plugin.editors.SQLEditor"); //$NON-NLS-1$
            editorPart.setText(buf.toString());
        } catch (SQLException e) {
            SQLExplorerPlugin.error(Messages.getString("CreateTableScriptAction.ErrorCreatExportScript"), e); //$NON-NLS-1$
        } catch (PartInitException e) {
            SQLExplorerPlugin.error(Messages.getString("CreateTableScriptAction.ErrorCreateExport"), e); //$NON-NLS-1$
        }
    }


    /**
     * Action is availble when a node is selected
     * 
     * @see net.sourceforge.sqlexplorer.dbstructure.actions.AbstractDBTreeContextAction#isAvailable()
     */
    public boolean isAvailable() {

        if (_selectedNodes.length != 0) {
            return true;
        }
        return false;
    }
}
