package com.te.top;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.Validate;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

public class TopTabCompleter implements TabCompleter {
	
	private List<Option> all_options;
	
	public void setOptions(List<Option> options) {
		this.all_options = options;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String lbl, String[] args) {
		if (all_options != null) {
			return getList(0, args, all_options);
		}
		return new ArrayList<>();
	}
	
	private List<String> getList(int index, String[] args, List<Option> all_options) {
		if (all_options == null) {
			return new ArrayList<>();
		}
		
		if (index + 1 >= args.length) {
			List<String> options = new ArrayList<>();
			for (Option op : all_options) {
				if (op.option.startsWith(args[index])) {
					options.add(op.option);
				}
				if (op.option.equalsIgnoreCase(args[index]) && op.suboptions != null) {
					options.add(op.option + " ");
				}
			}
			return options;
		}
		
		for (Option op : all_options) {
			if (op.option.equalsIgnoreCase(args[index])) {
				return getList(index + 1, args, op.suboptions);
			}
		}
		
		return null;
	}
	
	public static class Option {
		String option;
		List<Option> suboptions;
		
		public Option(String option) {
			this.option = option;
			suboptions = null;
		}
		
		public Option(String option, List<Option> suboptions) {
			Validate.notNull(suboptions);
			this.option = option;
			this.suboptions = suboptions;
		}
	}
}
