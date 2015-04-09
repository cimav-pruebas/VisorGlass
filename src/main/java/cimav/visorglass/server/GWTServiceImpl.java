/*
 * Copyright 2013 juan.calderon.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cimav.visorglass.server;

import cimav.visorglass.client.GWTService;
import cimav.visorglass.shared.Common;
import cimav.visorglass.shared.data.Arbol;
import cimav.visorglass.shared.data.Depto;
import cimav.visorglass.shared.data.Documento;
import cimav.visorglass.shared.data.TipoDocumento;
import cimav.visorglass.shared.data.Usuario;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.DirectoryStream;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.sax.BodyContentHandler;
import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonToken;
import org.xml.sax.SAXException;

/**
 *
 * @author juan.calderon
 */
public class GWTServiceImpl extends RemoteServiceServlet implements GWTService {

    private static final Logger log = Logger.getLogger(GWTServiceImpl.class.getCanonicalName());

    @Override
    public Usuario loginProfile(String tokenAutorizado) {

        String url = "https://www.googleapis.com/oauth2/v2/userinfo?alt=json&access_token=" + tokenAutorizado;

        final StringBuffer reader = new StringBuffer();
        try {
            final URL u = new URL(url);
            final URLConnection uc = u.openConnection();
            final int end = 1000;
            InputStreamReader isr;
            BufferedReader br = null;
            try {
                isr = new InputStreamReader(uc.getInputStream());
                br = new BufferedReader(isr);
                final int chk = 0;
                while ((url = br.readLine()) != null) {
                    if ((chk >= 0) && ((chk < end))) {
                        reader.append(url).append('\n');
                    }
                }
            } catch (final java.net.ConnectException cex) {
                reader.append(cex.getMessage());
            } catch (final Exception ex) {
                log.log(Level.SEVERE, ex.getMessage());
            } finally {
                try {
                    br.close();
                } catch (final Exception ex) {
                    log.log(Level.SEVERE, ex.getMessage());
                }
            }
        } catch (final Exception e) {
            log.log(Level.SEVERE, e.getMessage());
        }

        final Usuario usuario = new Usuario();
        try {
            final JsonFactory f = new JsonFactory();
            JsonParser jp;
            jp = f.createJsonParser(reader.toString());
            jp.nextToken();
            while (jp.nextToken() != JsonToken.END_OBJECT) {
                final String fieldname = jp.getCurrentName();
                jp.nextToken();
                if ("picture".equals(fieldname)) {
                    usuario.setPictureUrl(jp.getText());
                } else if ("name".equals(fieldname)) {
                    usuario.setNombre(jp.getText());
                } else if ("email".equals(fieldname)) {
                    usuario.setEmail(jp.getText());
                }
            }
        } catch (final JsonParseException e) {
            log.log(Level.SEVERE, e.getMessage());
        } catch (final IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }

        usuario.setEsAdministrador(esAdministrador(usuario.getCuenta()));

        String realPath = getServletContext().getRealPath("/");
        Common.loadSandBox(realPath);
        usuario.setServidorArchivosVigentes(Common.URL_SERVIDOR_ARCHIVOS_VIGENTES);

        return usuario;
    }

    @Override
    public Arbol cargarArbol(Usuario usuario) {
        List<Depto> deptos = new ArrayList<>();

        String realPath = getServletContext().getRealPath("/");
        Common.loadSandBox(realPath);

        //Path pathDeptos = FileSystems.getDefault().getPath(Common.DropBoxVigentesPath);
        try {
            for (Depto depto : usuario.getDeptos()) {
                String dirTipo = Common.DropBoxVigentesPath + "/" + depto.getCodigo();

                Path pathTipo = FileSystems.getDefault().getPath(dirTipo);

                DirectoryStream<Path> dsTipos = null;
                try {
                    // el depto de la estructura puede que no exista  en los Vigentes
                    dsTipos = Files.newDirectoryStream(pathTipo);
                } catch (IOException ex) {
                    Logger.getLogger(GWTServiceImpl.class.getName()).log(Level.INFO, "Path Estructura: {0} > No existe en Vigentes.", pathTipo.toString());
                }

                if (dsTipos != null) {
                    for (Path pathTipos : dsTipos) {
                        String codigoTipo = pathTipos.getFileName().toString();
                    
                        // solo para comprobar que mas o menos el tipo cumpla con la estructura
                        boolean esCorrecto = !(codigoTipo.contains(".") || codigoTipo.contains("_") || codigoTipo.contains(" ") || codigoTipo.length() > 4) ;
                        if (esCorrecto) {
                            TipoDocumento tipo = new TipoDocumento();
                            tipo.setCodigoDepto(depto.getCodigo());
                            tipo.setCodigo(codigoTipo);
                        
                            tipo.setDocs(this.cargaDocs(pathTipos.toString()));
                    
                            depto.getTipos().add(tipo);
                        }
                    }
                    deptos.add(depto);
                }
                
            }
        } catch (Exception eg) {
            Logger.getLogger(GWTServiceImpl.class.getName()).log(Level.SEVERE, " cargaArbol > " + eg.getMessage());
        }

        Arbol arbol = new Arbol(usuario, deptos);
        return arbol;
    }

    private List<Documento> cargaDocs(String dirTipo) {
        final List<Documento> result = new ArrayList<Documento>();
        
        final Parser parser = new AutoDetectParser(); // TODO AutoDetectParser es MUY LENTO
        String regexpFiles = "^((?i).*(txt|htm|html|xml|pdf|doc|docx|xls|xlsx|ppt|pptx))";

        // TODO poner en documentacio: Los Archivos deben tener CODIGO + Nombre + Extension conocida. 
        /***
         *  Los Archivos deben tener CODIGO + Nombre + Extension. 
         ***/
        
        try {
            final PathMatcher matcherExtension = FileSystems.getDefault().getPathMatcher("regex:" + regexpFiles);
            Path elPath = null;
            try {
                elPath = Paths.get(dirTipo);
            } catch (Exception t){
                Logger.getLogger(GWTServiceImpl.class.getName()).log(Level.SEVERE, " cargaDocs > " + dirTipo + " > " + t.getMessage());
            }
            Files.walkFileTree(elPath, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path pathFile, BasicFileAttributes attrs) throws IOException {

                    if (matcherExtension.matches(pathFile)) {

                        String vigentes = Common.DropBoxVigentesPath;
                        String sep = "/";
                        if (pathFile.toString().contains("\\")) {
                            // es Windows
                            sep = "\\";
                            vigentes = vigentes.replace("/", sep);
                            sep = "\\\\";
                        }
                        
                        int idx = pathFile.toString().indexOf(vigentes) + vigentes.length() + 1;
                        String subPath = pathFile.toString().substring(idx);
                        String subPaths[] = subPath.split(sep);
                        String depto = subPaths[0];
                        String tipo = subPaths[1];
                        String nombreCompleto = subPaths[2];
                        String codigoNombre[] = nombreCompleto.split(" ", 2);
                        String codigo = codigoNombre[0];
                        String nombre = "";
                        if (codigoNombre.length >= 2) {
                            nombre = codigoNombre[1];
                        }
                        
                        if (nombre.length() > 1) try {
                            //InputStream is = java.nio.file.Files.newInputStream(pathFile);
                            
                            String textContent = ""; //grabTextContent(parser, is);
                            
                            String codigosReferenciados = ""; //grabCodigos(textContent);

                            Documento doc = new Documento(depto, tipo, codigo, nombre, textContent, codigosReferenciados);
                                        
                            result.add(doc);

                            System.out.println(depto + " : " + tipo + " : " + codigo + " : " + nombre + " >> " + textContent);
//                            if (codigo.contains("CP01F02")) {
//                                System.out.println("Here");
//                            }
                         
                        } catch (ArrayIndexOutOfBoundsException ex) {
                            Logger.getLogger(GWTServiceImpl.class.getName()).log(Level.SEVERE, " cargaDocs.visitFile > " + pathFile + " > " + ex.getMessage());
                        } catch (Exception ex) {
                            Logger.getLogger(GWTServiceImpl.class.getName()).log(Level.SEVERE, " cargaDocs.visitFile > " + pathFile + " > " + ex.getMessage());
                        }
                    }
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (IOException ex) {
            System.out.println(">>> " + ex.getMessage());
        }
        
        return result;
    }
    
    private static String grabTextContent(final Parser parser, final InputStream is) throws Exception {
        // Todo con Tika
        String textContent;
        BodyContentHandler contenthandler = new BodyContentHandler(10000000);
        Metadata metadata = new Metadata();
        try {
            
            parser.parse(is, contenthandler, metadata, new ParseContext());
            textContent = contenthandler.toString();
            // sustituye caracteres No Alfanumericos por nada; excepto espacios. Respeta acentuacion europea.
            textContent = textContent.replaceAll("[^\\p{L}\\p{Nd}\\s\\-]", ""); 
            // sustituye espacios innecesarios por un solo espacio.
            textContent = textContent.replaceAll("\\s+", " "); 
            
        } catch (IOException ioe) { 
            textContent = "";
            throw new Exception(ioe.getMessage());
        } catch (SAXException saxe) { 
            textContent = "";
            throw new Exception(saxe.getMessage());
        } catch (TikaException te) { 
            textContent = "";
            throw new Exception(te.getMessage());
        }
        return textContent;
    }

    private boolean esAdministrador(String cuentaUsuario) {
        Boolean result = false;

        String realPath = getServletContext().getRealPath("/");
        Common.loadSandBox(realPath);

        try {
            Path pathAdmins = FileSystems.getDefault().getPath(Common.DropBoxAdministradoresPath);
            DirectoryStream<Path> dsAdmins = Files.newDirectoryStream(pathAdmins);
            for (Path pathPermiso : dsAdmins) {
                String cuentaAdmin = pathPermiso.getFileName().toString();
                if (cuentaAdmin.equals(cuentaUsuario)) {
                    result = true;
                    break;
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(GWTServiceImpl.class.getName()).log(Level.SEVERE, null, ex);

            String error = ex.getMessage();
            if (error.contains("timed")) {
                error = "ReadTimedOut";
            }
            if (error.contains("refused")) {
                error = "ConnectionRefused";
            }
        }

        return result;
    }
    
    private static String grabCodigos(String contenido) {
        String result = "";
        //Pattern pattern = Pattern.compile("([A-Za-z]{1,4}[0-9]{1,3}){2}"); //TODO el 1 debe ser 2
        Pattern pattern = Pattern.compile("[A-Za-z]{2,3}[0-9]{2}([A-Za-z]{1}[0-9]{2})?(-[0-9]{2})?"); // ej. CA01H01-04, CAB01, CA01-04
        Matcher matcher = pattern.matcher(contenido);
        while (matcher.find()) {
            // TODO Identificar aquellos que no sean del Cimav
            String referencia = matcher.group();
            // agregar las no repetidas
            result = !result.contains(referencia) ? result +  "|" + referencia : result;
        }        
        result = result.replaceFirst("\\|", ""); // elimina el 1er |
        //System.out.println(result);
        return result;
    }

    @Override
    public List<Depto> cargarPermisosDeptos() {
        List<Depto> deptos = new ArrayList<Depto>();

        String realPath = getServletContext().getRealPath("/");
        Common.loadSandBox(realPath);

        Path pathDeptos = FileSystems.getDefault().getPath(Common.DropBoxDeptosPath);
        try {
            DirectoryStream<Path> dsDeptos = Files.newDirectoryStream(pathDeptos);
            for (Path pathDepto : dsDeptos) {

                try {
                    String txt = pathDepto.getFileName().toString();

                    // solo para comprobar que mas o menos el depto cumpla con la estructura
                    boolean esCorrecto = !txt.contains(".") && txt.contains("-") && txt.contains(" ") && txt.length() >= 3;
                    if (esCorrecto) {
                    
                        Depto depto = new Depto();

                        String codigo = txt.split("-")[0];
                        String nombre = txt.split("-")[1];
                        depto.setCodigo(codigo.trim());
                        depto.setNombre(nombre.trim());

                        DirectoryStream<Path> dsPermisos = Files.newDirectoryStream(pathDepto);
                        for (Path pathPermiso : dsPermisos) {
                            String cuenta = pathPermiso.getFileName().toString();
                            depto.getCuentas().add(cuenta);
                        }

                        deptos.add(depto);
                    }
                } catch (Exception ex) {
                    // TODO no tiene ningun tipo de proteccion encaso de que el nombre/codigo cambie, ni sensible al case.
                    // si falla en subir un depto, simplemente se lo saltea
                    Logger.getLogger(GWTServiceImpl.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(GWTServiceImpl.class.getName()).log(Level.SEVERE, null, ex);

            String error = ex.getMessage();
            if (error.contains("timed")) {
                error = "ReadTimedOut";
            }
            if (error.contains("refused")) {
                error = "ConnectionRefused";
            }
        }

        return deptos;
    }

    @Override
    public List<TipoDocumento> cargarTipos() {
        List<TipoDocumento> tipos = new ArrayList<TipoDocumento>();

        String realPath = getServletContext().getRealPath("/");
        Common.loadSandBox(realPath);

        Path pathTipos = FileSystems.getDefault().getPath(Common.DropBoxTiposPath);
        try {
            DirectoryStream<Path> ds = Files.newDirectoryStream(pathTipos);
            for (Path p : ds) {
                System.out.println(p.getFileName() + "     >>       " + p.toAbsolutePath());

                TipoDocumento tipo = new TipoDocumento("NONE");
                String txt = p.getFileName().toString();
                try {
                    // TODO no tiene ningun tipo de proteccion encaso de que el nombre/codigo cambie, ni sensible al case.
                    String codigo = txt.split("-")[0];
                    String nombre = txt.split("-")[1];
                    tipo.setCodigo(codigo.trim());
                    tipo.setNombre(nombre.trim());

                    tipos.add(tipo);
                } catch (Exception ex) {

                }

            }
        } catch (IOException ex) {
            Logger.getLogger(GWTServiceImpl.class.getName()).log(Level.SEVERE, null, ex);

            String error = ex.getMessage();
            if (error.contains("timed")) {
                error = "ReadTimedOut";
            }
            if (error.contains("refused")) {
                error = "ConnectionRefused";
            }
        }

        return tipos;
    }

}
