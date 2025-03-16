package festp.top;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

public class TopTabCompleter implements TabCompleter {
	private TopManager manager;
	private List<Option> all_options;
	
	public TopTabCompleter(TopManager manager) {
		this.manager = manager;
	}
	
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
			if (suboptions == null)
				throw new NullPointerException();
			this.option = option;
			this.suboptions = suboptions;
		}
	}
	
	public void updateOptions() {
		setOptions(genOptions());
	}
	
	private List<TopTabCompleter.Option> genOptions() {
		List<Option> options = new ArrayList<>();
		
		for (RatingTop rt : manager.getTops()) {
			if (rt.places[0] == null) {
				continue;
			}
			
			boolean suboption = false;
			for (Option op : options) {
				if (op.option.equals(rt.main_activator)) {
					suboption = true;
					if (op.suboptions == null) {
						op.suboptions = new ArrayList<>();
					}
					Option new_op = new Option(rt.add_activator);
					op.suboptions.add(new_op);
				}
			}
			
			if (!suboption) {
				Option new_op = new Option(rt.main_activator);
				options.add(new_op);
				if (rt.add_activator != null) {
					if (new_op.suboptions == null) {
						new_op.suboptions = new ArrayList<>();
					}
					Option sub_op = new Option(rt.add_activator);
					new_op.suboptions.add(sub_op);
				}
			}
		}

		return options;
	}
}
