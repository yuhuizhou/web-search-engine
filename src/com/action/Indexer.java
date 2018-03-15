package com.action;


import org.apache.lucene.analysis.cjk.CJKAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

import java.io.*;

public class Indexer {


    private IndexWriter writer;

    private Indexer(String indexDir) throws IOException {

        FSDirectory dir = FSDirectory.open(new File(indexDir));
        writer = new IndexWriter(dir,
                new CJKAnalyzer(Version.LUCENE_CURRENT), true, IndexWriter.MaxFieldLength.UNLIMITED);


    }

    private void close() throws IOException {
        writer.close();
    }


    public int index(String dataDir, FileFilter filter) throws Exception {
        File[] files = new File(dataDir).listFiles();
        for (File f: files) {
            if (!f.isDirectory() && !f.isHidden() && f.exists() && f.canRead() &&
                    (filter == null || filter.accept(f))) {
                indexFile(f);
            }
        }
        return writer.numDocs();
    }

    private static class TextFilesFilter implements FileFilter {
        public boolean accept(File path) {
            return path.getName().toLowerCase().endsWith(".txt");

        }
    }

    private Document getDocument(File f) throws Exception {

        String title = null;
        String href =null;
        String summary =null;
        String content="";
       // StringBuffer content=null;
        try {
            FileReader fr=new FileReader(f);
            BufferedReader br=new BufferedReader(fr);
            title=br.readLine();
            summary=br.readLine();
            href=br.readLine();

            try {
                String line=br.readLine();
                while (line!=null) {
                   // content.append(line);
                    content+=line;
                    line=br.readLine();
                }
            }catch (IOException e){
                e.printStackTrace();
            }
        }catch (FileNotFoundException e){
            e.printStackTrace();
        }


        Document doc = new Document();
        doc.add(new Field("title", title, Field.Store.YES, Field.Index.NOT_ANALYZED));
        doc.add(new Field("href", href, Field.Store.YES, Field.Index.NOT_ANALYZED));
        doc.add(new Field("summary",  summary, Field.Store.YES, Field.Index.ANALYZED, Field.TermVector.WITH_POSITIONS_OFFSETS));
        doc.add(new Field("contents", new StringReader(content)));
        return doc;

    }
    private void indexFile(File f) throws Exception {
        System.out.println("Indexing " + f.getCanonicalPath());

        org.apache.lucene.document.Document doc = getDocument(f);
        writer.addDocument(doc);
    }

    public static void main(String[] args) throws IOException{

        String indexDir = "index/";
        String dataDir = "src/data/";
        long start = System.currentTimeMillis();
        Indexer indexer = new Indexer(indexDir);

        int numIndexed = 0;
        try {
            numIndexed = indexer.index(dataDir, new TextFilesFilter());
        } catch (IOException e){
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            indexer.close();
        }
        long end = System.currentTimeMillis();
        System.out.println("Indexing " + numIndexed + " files took "
                + (end - start) + " milliseconds");
    }

}
