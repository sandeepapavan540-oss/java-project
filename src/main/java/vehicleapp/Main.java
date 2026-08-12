package vehicleapp;



public class Main {
    public static void main(String[] args) {

        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            System.out.println("Nimbus Look and Feel setin can't: " + e.getMessage());
        }


        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new vehicleapp.ui.LoginForm().setVisible(true);
            }
        });
    }
}