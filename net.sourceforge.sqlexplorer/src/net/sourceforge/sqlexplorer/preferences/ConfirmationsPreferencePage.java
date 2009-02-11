package net.sourceforge.sqlexplorer.preferences;

import net.sourceforge.sqlexplorer.IConstants;
import net.sourceforge.sqlexplorer.Messages;
import net.sourceforge.sqlexplorer.IConstants.Confirm;

import org.eclipse.jface.preference.RadioGroupFieldEditor;

public class ConfirmationsPreferencePage extends AbstractPreferencePage{
	
	private static final String[][] CONFIRM_OPTIONS = {
		{ Messages.getString("Preferences.Confirm.Options.Yes"), Confirm.YES.toString() }, //$NON-NLS-1$
		{ Messages.getString("Preferences.Confirm.Options.No"), Confirm.NO.toString() }, //$NON-NLS-1$
		{ Messages.getString("Preferences.Confirm.Options.Ask"), Confirm.ASK.toString() } //$NON-NLS-1$
	};

	public ConfirmationsPreferencePage() {
        super(Messages.getString("Preferences.Confirm.Title"), GRID);  //$NON-NLS-1$
	}

	@Override
	protected void createFieldEditors() {
		addCheckBox(IConstants.CONFIRM_BOOL_CLOSE_ALL_CONNECTIONS, Messages.getString("Preferences.Confirm.CloseAllConnections")); //$NON-NLS-1$
		addCheckBox(IConstants.CONFIRM_BOOL_CLOSE_CONNECTION, Messages.getString("Preferences.Confirm.CloseConnection")); //$NON-NLS-1$
		addRadio(IConstants.CONFIRM_YNA_SAVING_INSIDE_PROJECT, Messages.getString("Preferences.Confirm.SavingInsideProject")); //$NON-NLS-1$
		addCheckBox(IConstants.CONFIRM_BOOL_SHOW_DIALOG_ON_QUERY_ERROR, Messages.getString("Preferences.Confirm.ShowDialogOnQueryError")); //$NON-NLS-1$
		addCheckBox(IConstants.CONFIRM_BOOL_WARN_LARGE_MAXROWS, Messages.getString("Preferences.Confirm.WarnLargeMaxrows")); //$NON-NLS-1$
	}
	
	private void addRadio(String preferenceId, String caption) {
		addField(new RadioGroupFieldEditor(preferenceId, caption, 3, CONFIRM_OPTIONS, getFieldEditorParent()));
	}
	
	private void addCheckBox(String preferenceId, String caption) {
		addField(new BooleanFieldEditor(preferenceId, caption, getFieldEditorParent()));
	}
}
