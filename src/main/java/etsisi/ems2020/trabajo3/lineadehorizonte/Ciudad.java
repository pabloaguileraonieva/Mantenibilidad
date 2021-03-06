package etsisi.ems2020.trabajo3.lineadehorizonte;

import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;


/*
 Clase fundamental.
 Sirve para hacer la lectura del fichero de entrada que contiene los datos de como
 están situados los edificios en el fichero de entrada. xi, xd, h, siendo. Siendo
 xi la coordenada en X origen del edificio iésimo, xd la coordenada final en X, y h la altura del edificio.
 
 */
public class Ciudad {
	
    private ArrayList <Edificio> ciudad;

    public Ciudad()
    {    
    	ciudad = new ArrayList <Edificio>();
    }
    
    public Ciudad(int n) {
    	ciudad = new ArrayList <Edificio>();
    	int xi,y,xd;
    	for(int i = 0; i < n; i++)
    	{
    		xi=(int)(Math.random()*100);
    		y=(int)(Math.random()*100);
    		xd=(int)(xi+(Math.random()*100));
    		this.addEdificio(new Edificio(xi,y,xd));
    	}

    }
        
    public Edificio getEdificio(int i) {
        return (Edificio)this.ciudad.get(i);
    }
    
       
    public void addEdificio (Edificio e)
    {
        ciudad.add(e);
    }
    public void removeEdificio(int i)
    {
        ciudad.remove(i);
    }
    
    public int size()
    {
        return ciudad.size();
    }
    
    public LineaHorizonte getLineaHorizonte()
    {
    	// pi y pd, representan los edificios de la izquierda y de la derecha.
        int pi = 0;                       
        int pd = ciudad.size()-1;      
        return crearLineaHorizonte(pi, pd);  
    }
    
public LineaHorizonte crearLineaHorizonte(int pi, int pd)
{
LineaHorizonte linea = new LineaHorizonte(); // LineaHorizonte de salida
Punto p1 = new Punto();   // punto donde se guardara en su X la Xi del efificio y en su Y la altura del edificio
Punto p2 = new Punto();   // punto donde se guardara en su X la Xd del efificio y en su Y le pondremos el valor 0
Edificio edificio = new Edificio();    
        
// Caso base, la ciudad solo tiene un edificio, el perfil es el de ese edificio. 
if(pi==pd) 
{
edificio = this.getEdificio(pi); // Obtenemos el único edificio y lo guardo en b
// En cada punto guardamos la coordenada X y la altura.
p1.setX(edificio.getXi());       
p1.setY(edificio.getY());        // guardo la altura
p2.setX(edificio.getXd());       
p2.setY(0);                      // como el edificio se compone de 3 variables, en la Y de p2 le añadiremos un 0
// Añado los puntos a la línea del horizonte
linea.addPunto(p1);      
linea.addPunto(p2);
}
else
{
// Edificio mitad
int medio=(pi+pd)/2;

LineaHorizonte s1 = this.crearLineaHorizonte(pi,medio);  
LineaHorizonte s2 = this.crearLineaHorizonte(medio+1,pd);
conjuntoPuntos puntos=new conjuntoPuntos();
linea = LineaHorizonteFussion(s1,s2,puntos); 
}
return linea;
    }
    
    /**
     * Función encargada de fusionar los dos LineaHorizonte obtenidos por la técnica divide y
     * vencerás. Es una función muy compleja ya que es la encargada de decidir si un
     * edificio solapa a otro, si hay edificios contiguos, etc. y solucionar dichos
     * problemas para que el LineaHorizonte calculado sea el correcto.
     */
    public LineaHorizonte LineaHorizonteFussion(LineaHorizonte s1,LineaHorizonte s2, conjuntoPuntos puntos)
    {
        historialAlturas aux=new historialAlturas();
    	LineaHorizonte salida = new LineaHorizonte(); // LineaHorizonte de salida

        
        imprimirLineas(s1,s2);
        
        //Mientras tengamos elementos en s1 y en s2
        while ((!s1.isEmpty()) && (!s2.isEmpty())) 
        {
        	lineaHorizonteFussionExtra(s1,s2,puntos,aux,salida);
        }
        while ((!s1.isEmpty())) //si aun nos quedan elementos en el s1
        {
            puntos.setPaux(s1.getPunto(0)); // guardamos en paux el primer punto
            
            if (puntos.getPaux().getY()!=aux.getPrev()) // si paux no tiene la misma altura del segmento previo
            {
                salida.addPunto(puntos.getPaux()); // lo añadimos al LineaHorizonte de salida
                aux.setPrev(puntos.getPaux().getY());    // y actualizamos prev
            }
            s1.borrarPunto(0); // en cualquier caso eliminamos el punto de s1 (tanto si se añade como si no es valido)
        }
        while((!s2.isEmpty())) //si aun nos quedan elementos en el s2
        {
            puntos.setPaux(s2.getPunto(0)); // guardamos en paux el primer punto
           
            if (puntos.getPaux().getY()!=aux.getPrev()) // si paux no tiene la misma altura del segmento previo
            {
                salida.addPunto(puntos.getPaux()); // lo añadimos al LineaHorizonte de salida
                aux.setPrev(puntos.getPaux().getY());    // y actualizamos prev
            }
            s2.borrarPunto(0); // en cualquier caso eliminamos el punto de s2 (tanto si se añade como si no es valido)
        }
        return salida;
    }
    
    public void lineaHorizonteFussionExtra(LineaHorizonte s1,LineaHorizonte s2, conjuntoPuntos puntos, historialAlturas aux, LineaHorizonte salida)
    {
        puntos.paux = new Punto();  // Inicializamos la variable paux
        puntos.setP1(s1.getPunto(0)); // guardamos el primer elemento de s1
        puntos.setP2(s2.getPunto(0)); // guardamos el primer elemento de s2

        if (puntos.getP1().getX() < puntos.getP2().getX())  // si X del s1 es menor que la X del s2
        {
        	lineaHorizonteFussionExtra1(puntos, aux, salida, s1);
        }
        else if (puntos.getP1().getX() > puntos.getP2().getX()) // si X del s1 es mayor que la X del s2
        {
        	lineaHorizonteFussionExtra3(puntos, aux, salida, s2);
        }
        else // si la X del s1 es igual a la X del s2
        {
        	lineaHorizonteFussionExtra2(puntos,aux, salida, s1,s2);
        }
    	
    }
    
    public void lineaHorizonteFussionExtra1(conjuntoPuntos puntos, historialAlturas aux, LineaHorizonte salida, LineaHorizonte s)
    {
        puntos.getPaux().setX(puntos.getP1().getX());                // guardamos en paux esa X
        puntos.paux.setY(Math.max(puntos.getP1().getY(), aux.getS2y())); // y hacemos que el maximo entre la Y del s1 y la altura previa del s2 sea la altura Y de paux
        
        if (puntos.paux.getY()!=aux.getPrev()) // si este maximo no es igual al del segmento anterior
        {
            salida.addPunto(puntos.paux); // añadimos el punto al LineaHorizonte de salida
            aux.setPrev(puntos.paux.getY());    // actualizamos prev
        }
        aux.setS1y(puntos.p1.getY());   // actualizamos la altura s1y
        s.borrarPunto(0); // en cualquier caso eliminamos el punto de s1 (tanto si se añade como si no es valido)
    }
    public void lineaHorizonteFussionExtra3(conjuntoPuntos puntos, historialAlturas aux, LineaHorizonte salida, LineaHorizonte s)
    {
        puntos.getPaux().setX(puntos.getP2().getX());                // guardamos en paux esa X
        puntos.paux.setY(Math.max(puntos.getP2().getY(), aux.getS2y())); // y hacemos que el maximo entre la Y del s1 y la altura previa del s2 sea la altura Y de paux
        
        if (puntos.paux.getY()!=aux.getPrev()) // si este maximo no es igual al del segmento anterior
        {
            salida.addPunto(puntos.paux); // añadimos el punto al LineaHorizonte de salida
            aux.setPrev(puntos.paux.getY());    // actualizamos prev
        }
        aux.setS1y(puntos.p2.getY());   // actualizamos la altura s1y
        s.borrarPunto(0); // en cualquier caso eliminamos el punto de s1 (tanto si se añade como si no es valido)
    }
    public void lineaHorizonteFussionExtra2(conjuntoPuntos puntos, historialAlturas aux, LineaHorizonte salida, LineaHorizonte s1, LineaHorizonte s2)
    {
        if ((puntos.p1.getY() > puntos.p2.getY()) && (puntos.p1.getY()!=aux.getPrev())) // guardaremos aquel punto que tenga la altura mas alta
        {
            salida.addPunto(puntos.p1);
            aux.setPrev(puntos.p1.getY());
        }
        if ((puntos.p1.getY() <= puntos.p2.getY()) && (puntos.p2.getY()!=aux.getPrev()))
        {
            salida.addPunto(puntos.p2);
            aux.setPrev(puntos.p2.getY());
        }
        aux.setS1y(puntos.p1.getY());   // actualizamos la s1y e s2y
        aux.setS2y(puntos.p2.getY());
        s1.borrarPunto(0); // eliminamos el punto del s1 y del s2
        s2.borrarPunto(0);
    }

    /*
     Método que carga los edificios que me pasan en el
     archivo cuyo nombre se encuentra en "fichero".
     El formato del fichero nos lo ha dado el profesor en la clase del 9/3/2020,
     pocos días antes del estado de alarma.
     */

    public void cargarEdificios (String fichero) {	
        try {
            int xi, y, xd;
            Scanner sr = new Scanner(new File(fichero));
            while(sr.hasNext()) {
                xi = sr.nextInt();
                xd = sr.nextInt();
                y = sr.nextInt();
                Edificio Salida = new Edificio(xi, y, xd);
                this.addEdificio(Salida);
            }
        }
        catch(Exception e){}     
    }
    
    public void imprimirLineas(LineaHorizonte s1, LineaHorizonte s2)
    {
        System.out.println("==== S1 ====");
        s1.imprimir();
        System.out.println("==== S2 ====");
        s2.imprimir();
        System.out.println("\n");
    }
}
