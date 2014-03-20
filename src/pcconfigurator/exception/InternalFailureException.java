/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package pcconfigurator.exception;

/**
 *
 * @author davidkaya
 */
public class InternalFailureException extends RuntimeException {
    public InternalFailureException(String message){
        super(message);
    }
    
    public InternalFailureException(Throwable cause){
        super(cause);
    }
    
    public InternalFailureException(String message,Throwable cause){
        super(message,cause);
    }
}
