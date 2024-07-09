
public class Vetor {
    double x;
    double y;
    double norma;
    double angulo;
    
    //Construtor do vetor
    Vetor(double x, double y){      
        this.x = x;
        this.y = y;
        atualiza();
    }

    //Calcula novamente a norma do vetor e seu ângulo em relação ao eixo x
    void atualiza(){
        this.norma = Math.sqrt(x*x + y*y);
        this.angulo = Math.atan2(x,y);

        if(Math.abs(x)<0.001)x=0;
        if(Math.abs(y)<0.001)y=0;
    }

    //Calcula e retorna o vetor diretor de um vetor dado
    //  Vetor diretor: vetor unitário com mesmo sentido e direção
    Vetor diretor(){
        return new Vetor(x/norma, y/norma);
    }

    //Soma dois vetores
    static Vetor soma(Vetor u, Vetor v){
        return new Vetor(u.x+v.x, u.y+v.y);
    }

    //Subtrai o segundo vetor do primeiro
    static Vetor subtracao(Vetor u, Vetor v){
        return new Vetor(u.x-v.x, u.y-v.y);
    }

    //Multiplica um vetor por um número real
    static Vetor multiplicacao(Vetor v, double f){
        return new Vetor(v.x*f, v.y*f);
    }

    //Calcula o produto escalar de dois vetores
    static double produtoEscalar(Vetor u, Vetor v){
        double resultado = u.x*v.x + u.y*v.y;
        return resultado;
    }


}
