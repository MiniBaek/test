package mytest4.info;

import java.util.List;

public class CommandList {

	private List<CommandInfo2> commands;

	public List<CommandInfo2> getCommands() {
		return commands;
	}

	public void setCommands(List<CommandInfo2> commands) {
		this.commands = commands;
	}
	
	public class CommandInfo2 {

		private String command;
		private String forward_command;
		
		public String getCommand() {
			return command;
		}
		public void setCommand(String command) {
			this.command = command;
		}
		public String getForward_command() {
			return forward_command;
		}
		public void setForward_command(String forward_command) {
			this.forward_command = forward_command;
		}
	}

}
