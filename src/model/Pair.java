/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package model;

/**
 *
 * @author installer
 */
public class Pair<E,F> {
    public E first;
    public F second;
    public Pair(){}
    public Pair(E e, F f){
        first = e;
        second = f;
    }
}
