/*******************************************************************************
 Copyright (c) 2012 kopson kopson.piko@gmail.com

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 *******************************************************************************/

package kparserbenchmark.projectexplorer;

import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.eclipse.swt.dnd.ByteArrayTransfer;
import org.eclipse.swt.dnd.TransferData;

/**
 * Class for serializing gadgets to/from a byte array
 * 
 * @author Kopson
 */
public class GadgetTransfer extends ByteArrayTransfer {

	// Logger instance
	private static final Logger LOG = Logger.getLogger(GadgetTransfer.class
			.getName());
		
	private static GadgetTransfer instance = new GadgetTransfer();
	private static final String TYPE_NAME = "gadget-transfer-format";
	private static final int TYPEID = registerType(TYPE_NAME);

	/**
	 * Returns the singleton gadget transfer instance.
	 */
	public static GadgetTransfer getInstance() {
		return instance;
	}

	/**
	 * Avoid explicit instantiation
	 */
	private GadgetTransfer() {
	}

	@Override
	protected int[] getTypeIds() {
		return new int[] { TYPEID };
	}

	@Override
	protected String[] getTypeNames() {
		return new String[] { TYPE_NAME };
	}

	@Override
	protected void javaToNative(Object object, TransferData transferData) {
		byte[] bytes = toByteArray((ProjectLeaf[]) object);
		if (bytes != null)
			super.javaToNative(bytes, transferData);
	}

	@Override
	protected Object nativeToJava(TransferData transferData) {
		byte[] bytes = (byte[]) super.nativeToJava(transferData);
		return fromByteArray(bytes);
	}
	
	/**
	 * Convert byte array to array of Project items
	 * 
	 * @param bytes
	 * @return Returns ProjectLeaf array or null
	 */
	protected ProjectLeaf[] fromByteArray(byte[] bytes) {
		DataInputStream in = new DataInputStream(
				new ByteArrayInputStream(bytes));

		try {
			/* read number of gadgets */
			int n = in.readInt();
			/* read gadgets */
			ProjectLeaf[] gadgets = new ProjectLeaf[n];
			for (int i = 0; i < n; i++) {
				ProjectLeaf gadget = readGadget(in);
				if (gadget == null) {
					return null;
				}
				gadgets[i] = gadget;
			}
			return gadgets;
		} catch (IOException e) {
			return null;
		}
	}

	/**
	 * Reads and returns a single gadget from the given stream.
	 * 
	 * Gadget serialization format is as follows: 
	 * (String) name of gadget
	 * (String) gadget path
	 * (String) gadget type
	 * (int) number of child gadgets 
	 * (Gadget) child 1 ... repeat for each child
	 * 
	 * @param gadget Gadget to read
	 * @dataIn input stream
	 */
	private ProjectLeaf readGadget(DataInputStream dataIn)
			throws IOException {
		
		String name = dataIn.readUTF();
		String path = dataIn.readUTF();
		String type = dataIn.readUTF();
		int n = dataIn.readInt();
		ProjectLeaf newGadget = new ProjectLeaf(ProjectItem.ItemTypes.valueOf(type), null, path, name);
		System.out.println("readGadget " + newGadget.getName());
		for (int i = 0; i < n; i++) {
			ProjectLeaf child = readGadget(dataIn);
			System.out.println("readGadget " + child.getName());
			
			if(newGadget != null) {
				newGadget.addChild(child);
			}
		}
		return newGadget;
	}

	/**
	 * Transfer data is an array of gadgets. 
	 * 
	 * Serialized version is: 
	 * (int) number of gadgets 
	 * (Gadget) gadget 1 (Gadget) gadget 2 ... repeat for
	 * each subsequent gadget see writeGadget for the (Gadget) format.
	 * 
	 * @param gadgets Gadgrt array to save
	 */
	protected byte[] toByteArray(ProjectLeaf[] gadgets) {

		ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
		DataOutputStream out = new DataOutputStream(byteOut);
		byte[] bytes = null;
		try {
			/* write number of markers */
			out.writeInt(gadgets.length);

			/* write markers */
			for (int i = 0; i < gadgets.length; i++) {
				writeGadget((ProjectLeaf) gadgets[i], out);
			}
			out.close();
			bytes = byteOut.toByteArray();
		} catch (IOException e) {
			LOG.log(Level.WARNING, "Can't serialize Project Item objects " + e.getMessage());
		}
		return bytes;
	}

	/**
	 * Writes the given gadget to the stream.
	 * 
	 * Gadget serialization format is as follows: 
	 * (String) name of gadget
	 * (String) gadget path
	 * (String) gadget type
	 * (int) number of child gadgets 
	 * (Gadget) child 1 ... repeat for each child
	 * 
	 * @param gadget Object to save
	 * @param dataOut output stream
	 */
	private void writeGadget(ProjectLeaf gadget, DataOutputStream dataOut)
			throws IOException {
		
		System.out.println("writeGadget " + gadget.getName());
		dataOut.writeUTF(gadget.getName());
		dataOut.writeUTF(gadget.getPath());
		dataOut.writeUTF(gadget.type.name());
		
		ProjectLeaf[] children = (ProjectLeaf[]) gadget.getChildren();
		if (children == null) {
			dataOut.writeInt(0);
			System.out.println("no children");
		} else {
			dataOut.writeInt(children.length);
			for (int i = 0; i < children.length; i++) {
				writeGadget(children[i], dataOut);
				System.out.println("children " + children[i].getName());
			}
		}
	}
}