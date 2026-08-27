package commands;

import exceptions.YourHandException;
import storage.Storage;
import tasks.TaskList;
import ui.Ui;

/** Displays tasks whose descriptions contain a search keyword. */
public class FindCommand extends Command {
    private final String keyword;

    public FindCommand(String keyword) {
        this.keyword = keyword;
    }

    @Override
    public void execute(TaskList taskList, Ui ui, Storage storage) throws YourHandException {
        ui.showSearchResults(taskList, keyword);
    }
}
