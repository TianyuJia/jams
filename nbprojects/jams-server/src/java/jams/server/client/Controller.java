/*
 * Controller.java
 * Created on 20.04.2014, 14:48:42
 *
 * This file is part of JAMS
 * Copyright (C) FSU Jena
 *
 * JAMS is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 *
 * JAMS is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with JAMS. If not, see <http://www.gnu.org/licenses/>.
 *
 */

package jams.server.client;

import com.sun.istack.logging.Logger;
import jams.server.entities.Job;
import jams.server.entities.User;
import jams.server.entities.Users;
import jams.server.entities.Workspace;
import java.io.File;
import java.util.Date;
import java.util.Map;
import java.util.Random;
import java.util.logging.Level;

/**
 *
 * @author christian
 */
public class Controller {
    String SEPARATOR = "**********************";
    HTTPClient client;
    User user = null;
    String serverURL;
    
    public Controller(HTTPClient client, String serverURL){
        this.client = client;
        this.serverURL = serverURL;
    }
    
    protected void log(String message){
        Logger.getLogger(Controller.class).log(Level.ALL, message);
    }
        
    public void connect(String userName, String password) throws JAMSClientException{
        log(SEPARATOR + "\nTrying to connect .. ");
        user = (User)client.connect(serverURL + "/user/login?login=" + userName + "&password=" + password, User.class);
        
        if (user == null) {
            throw new JAMSClientException("Connection to server was not established!", JAMSClientException.ExceptionType.UNKNOWN, null);
        }
        log("INFO: Login successful\n");        
    }
    
    public FileController getFileController(){
        return new FileController(client, serverURL);
    }
    
    public WorkspaceController getWorkspaceController(){
        return new WorkspaceController(client, serverURL);
    }
    
    public UserController getUserController(){
        return new UserController(client, serverURL);
    }
    
    public JobController getJobController(){
        return new JobController(client, serverURL);
    }
    
    public void close(){
        this.client.close();
    }
    
    public static void main(String[] args) throws JAMSClientException {
        Controller client = new Controller(new HTTPClient(), "http://localhost:8080/jams-server/webresources");        
        client.connect("Blubb", "test");

        User user = new User(5);
        user.setAdmin(1);
        user.setEmail("blubb@gmx.de");
        user.setId(5);
        user.setLogin(Integer.toString((new Random()).nextInt()));
        user.setName("Der Marshmellow Mann");
        user.setPassword("test");

        User newUser = client.getUserController().createUser(user);
        if (newUser != null) {
            System.out.println("User was created successfully with id " + newUser.getId());
            newUser = client.getUserController().removeUser(newUser.getId());
            if (newUser != null){
                System.out.println("User was successfully deleted");
            }
            
        }
        Users users = client.getUserController().getAllUsers();
        if (users != null) {
            System.out.println("List of all users is now:" + users.toString());
        }
        Users result = client.getUserController().getUsersInRange(0, 1);
        if (result != null) {
            System.out.println("Users with id 0 and 1 are" + result.toString());
        }

        //Files
        jams.server.entities.File f1 = client.getFileController().uploadFile(new File("E:/tmp/style.css"));
        if (f1 != null){
            System.out.println("File with id " + f1.getId() + " was uploaded successfully!");
        }
        File f2 = new File("E:/tmp/show_log.php");
        File f3 = new File("E:/tmp/successful.php");
        Map<File, jams.server.entities.File> map = client.getFileController().uploadFile(new File[]{f2, f3});
        if (map.get(f2)!=null && map.get(f3)!=null){
            System.out.println("File with ids " + map.get(f2).getId() + "/" + map.get(f3).getId() + " were uploaded successfully!");
        }
        //Workspace
        Workspace ws = new Workspace();
        ws.setId(0);
        ws.setName("TestWs");
        ws.setCreationDate(new Date());
        ws = client.getWorkspaceController().create(ws);        
        if (ws != null ) {
            System.out.println("Workspace with id " + ws.getId() + " was created successfully!");
            client.getWorkspaceController().assignFile(ws, f1,4,"/test/test1.dat");
            client.getWorkspaceController().assignFile(ws, map.get(f2),4,"/test/test2.dat");
            client.getWorkspaceController().assignFile(ws, map.get(f3),4,"/test/test3.dat");
            
            client.getWorkspaceController().downloadFile(new File("E:/tmp/"),ws, f1);
                                    
            ws = client.getWorkspaceController().remove(ws);
            if (ws != null){
                System.out.println("Workspace with id " + ws.getId() + " was removed successfully!");
            }            
        }
        
        Workspace ws2 = client.getWorkspaceController().uploadWorkspace(
                "Wilde Gera", 
                new File("E:/ModelData/Europe/Germany/Thüringen/J2000 Wilde Gera/"), 
                new File("E:/ModelData/Europe/Germany/Thüringen/J2000 Wilde Gera/j2k_gehlberg.jam"), 
                new File[]{new File("E:\\JAMS_rep\\JAMS\\lib")}, 
                new File[]{new File("E:\\JAMS_rep\\JAMS\\nbprojects\\jams-ui\\dist\\")},
                null);
        if (ws2 != null){
            System.out.println("Workspace of Wilde Gera Model was uploaded successfully with id " + ws2.getId());
            client.getWorkspaceController().downloadWorkspace(new File("E:/test_client/" + ws2.getId() + "/"),ws2);
            System.out.println("Workspace of Wilde Gera Model was downloaded to E:/test_client/" + ws2.getId());
            Job job = client.getJobController().create(ws2);
            if (job != null){
                System.out.println("Wilde Gera Model started successfully! " + "Job Id is: " + job.getId());
            }
            while (client.getJobController().getState(job).isActive()){
                System.out.println("Job with id " + job.getId() + " still running!");
                try {
                    Thread.sleep(500);
                } catch (InterruptedException ex) {ex.printStackTrace();}
            }
            System.out.println("Job with id " + job.getId() + " finished after " + client.getJobController().getState(job).getDuration() + " ms." );
            Workspace ws3 = job.getWorkspace();
            System.out.println("Workspace of " +ws3.getName()+" Model is downloading!");
            client.getWorkspaceController().downloadWorkspace(new File("E:/test_client/"+ws3.getName()),ws3);
            client.getJobController().delete(job);                
            System.out.println("Job with id " + job.getId() + " deleted");
            client.getWorkspaceController().remove(ws2);
            System.out.println("Workspace of Wilde Gera Model was deleted!");
        }        
        
        client.close();
    }
}
