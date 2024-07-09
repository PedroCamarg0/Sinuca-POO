import javax.swing.*;
import java.awt.event.*;
import java.awt.*;

import java.io.*;
import javax.imageio.*;

public class Menu extends JFrame implements ActionListener {
    
    //Coloca imagem no fundo do menu
    Image IMG;
    JPanel p = new JPanel(new GridBagLayout()){         
        public void paintComponent(Graphics g){
            super.paintComponent(g);
            try{
                IMG = ImageIO.read(new File("imagemMenu.jpg"));
            }catch(Exception e){
                System.out.println("Erro: " + e);
            };
            g.drawImage(IMG, 0, 0,626, 418, this);
            
        }
    };

    //Cria grid com os botões do menu
    JPanel p1 = new JPanel(new GridLayout(2, 1)); 
    JButton jogar = new JButton("Jogar");
    JButton sair = new JButton("Sair");
  
   
    //Tratamento dos botões
    public void actionPerformed(ActionEvent e) {    
        if (e.getSource() == jogar) {   // Se clicar em jogar, cria jogo e fecha o menu
            new Jogo(); 
            this.dispose();
        } else if (e.getSource() == sair) { //Se clica em sair, fecha tudo
            this.dispose();
            System.exit(0);
        }
    }

    
    //Construtor do Menu
    Menu(){

        //Atribui tamanho dos botões
        Dimension buttonSize = new Dimension(100, 50); 
        jogar.setPreferredSize(buttonSize);
        sair.setPreferredSize(buttonSize);
        
        //Atribui os Actions Listeners
        jogar.addActionListener(this);  
        sair.addActionListener(this);

        //Adiciona os componentes a tela
        p1.add(jogar);      
        p1.add(sair);
        p.add(p1, new GridBagConstraints());
        add(p);

        //Configura o frame
        setVisible(true);
        setSize(626, 418);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null); 
        setResizable(false);
    }

}
