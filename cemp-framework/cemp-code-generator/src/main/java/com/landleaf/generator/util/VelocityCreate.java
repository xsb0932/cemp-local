package com.landleaf.generator.util;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

import java.io.*;
import java.util.Properties;

public class VelocityCreate {
	public static String fill(VelocityContext context, String tmFile) {
		String result = "";
		try {
			Properties p = new Properties();
			String tmdir = System.getProperty("user.dir")+"/cemp-framework/cemp-code-generator/src/main/resources/template";
			p.setProperty("file.resource.loader.path", tmdir);
			System.out.println(tmdir);
			// 引擎
			VelocityEngine ve = new VelocityEngine();
			ve.init(p);

			// 模板
			Template t = ve.getTemplate(tmFile, "UTF-8");
			// 将输入写入模板中
			StringWriter writer = new StringWriter();
			t.merge(context, writer);
			result = writer.toString();
			writer.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	public static void writeFile(String outPath, String packageName, String fileName, String fileContent) throws IOException {
		StringBuilder dirBuilder = new StringBuilder(outPath);
		if (!outPath.endsWith("/")) {
			dirBuilder.append("/");
		}
		String[] paths = packageName.split("\\.");
		for (String path : paths) {
			dirBuilder.append(path).append("/");
		}
		dirBuilder.append(fileName);
		File f = new File(dirBuilder.toString());
		if (!f.getParentFile().exists()) {
			f.getParentFile().mkdirs();
		}
		BufferedWriter writer = new BufferedWriter(new FileWriter(f));
		writer.write(fileContent);
		writer.close();
	}
}
