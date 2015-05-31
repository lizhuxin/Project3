import javax.swing.JOptionPane;


public class DimensionSelectorPanel extends JOptionPane {
	
	public static int getDimensions() {
		
		int dimensions = 0;
		int proposedDimensions = 0;
		String input;
		boolean acceptableDimensions = false;
		
		while(!acceptableDimensions) { //input dialog doesn't go away when you press cancel
			input = JOptionPane.showInputDialog("Enter size of minesweeper grid");
			try {
				proposedDimensions = Integer.parseInt(input);
				if(proposedDimensions * proposedDimensions >= 40) {
					acceptableDimensions = true;
					dimensions = proposedDimensions;
					System.out.println(dimensions);
				} else {
					JOptionPane.showMessageDialog(null, "Error: grid must be able to accomodate 40 mines");
				}
			} catch(Exception e) {
				JOptionPane.showMessageDialog(null, "Error: invalid input. Grid dimension must be stated as a single integer.");
			}
		}
		
		return dimensions;
	}
}
