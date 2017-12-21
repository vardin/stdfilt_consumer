package cuda_matrix;

import static jcuda.driver.JCudaDriver.cuCtxCreate;

import static jcuda.driver.JCudaDriver.cuCtxDestroy;
import static jcuda.driver.JCudaDriver.cuCtxSynchronize;
import static jcuda.driver.JCudaDriver.cuDeviceGet;
import static jcuda.driver.JCudaDriver.cuInit;
import static jcuda.driver.JCudaDriver.cuLaunchKernel;
import static jcuda.driver.JCudaDriver.cuMemAlloc;
import static jcuda.driver.JCudaDriver.cuMemFree;
import static jcuda.driver.JCudaDriver.cuMemcpyDtoH;
import static jcuda.driver.JCudaDriver.cuMemcpyHtoD;
import static jcuda.driver.JCudaDriver.cuModuleGetFunction;
import static jcuda.driver.JCudaDriver.cuModuleLoad;

import jcuda.Pointer;
import jcuda.Sizeof;
import jcuda.driver.CUcontext;
import jcuda.driver.CUdevice;
import jcuda.driver.CUdeviceptr;
import jcuda.driver.CUfunction;
import jcuda.driver.CUmodule;
import jcuda.driver.JCudaDriver;
import JCudaSamplesUtils.*;

public class cuda_matrix {
		
	int width;
	int numElemnets;
	CUfunction function;
	int blockSizeX;
	int gridSizeX;
	Pointer kernelParameters;
	byte hostInputA[];
	byte hostInputB[];
	byte hostOutput[];
	CUdeviceptr deviceInputA;
	CUdeviceptr deviceInputB;
	CUdeviceptr deviceOutput;
	int block_size=16;				//block size
	CUdevice device;
	CUcontext context;
	CUmodule module;	
	
	public cuda_matrix(int input_width){
	
		
		// Enable exceptions and omit all subsequent error checks
		JCudaDriver.setExceptionsEnabled(true);

		// Create the PTX file by calling the NVCC
		String ptxFileName = JCudaSamplesUtils.preparePtxFile("src/main/resources/kernels/stdfilt.ptx");

		// Initialize the driver and create a context for the first device.
		cuInit(0);
		
		
		device = new CUdevice();
		cuDeviceGet(device, 0);
		context = new CUcontext();
		cuCtxCreate(context, 0, device);
		

		// Load the ptx file.
		module = new CUmodule();
//		cuModuleLoad(module, "src/main/resources/kernels/JCudaVectorMatrixMultiplication.ptx"); //window version
		cuModuleLoad(module, "target/classes/kernels/stdfilt.ptx"); //linux version

		// Obtain a function pointer to the "add" function.
		function = new CUfunction();
		cuModuleGetFunction(function, module, "stdfilt");

		this.width = input_width*input_width;
		numElemnets = (width) * (width);
		
//		System.out.println("constructor has finished! ");
			
	}
	
	public void prepare_cuda_memory(byte[] input)
	{
		// Allocate and fill the host input data
	
		hostInputA = new byte[numElemnets];
//		hostInputB = new byte[numElemnets];
		hostOutput = new byte[numElemnets];
			
		
		for (int i = 0; i < numElemnets; i++) {
					hostInputA[i] = (byte) (i%127);
		//			hostInputB[i] = (byte)(i%127);
				} 
			
	//			hostInputA = input.clone();
	//			hostInputB = input.clone();
		
	//			System.out.println("hostInputA[0] ="+hostInputA[0]);
	//			System.out.println(hostInputA[0]);
	//			System.out.println(hostInputA[1]);
				
				// Allocate the device input data, and copy the
				// host input data to the device
								
		
				deviceInputA = new CUdeviceptr();
				cuMemAlloc(deviceInputA, numElemnets * Sizeof.BYTE);
	//			deviceInputB = new CUdeviceptr();
	//			cuMemAlloc(deviceInputB, numElemnets * Sizeof.BYTE);
				deviceOutput = new CUdeviceptr();
				cuMemAlloc(deviceOutput, numElemnets * Sizeof.BYTE);
		
	//			System.out.println("before Memcpy!");
				
				cuMemcpyHtoD(deviceInputA, Pointer.to(hostInputA), numElemnets * Sizeof.BYTE);

	//			cuMemcpyHtoD(deviceInputB, Pointer.to(hostInputB), numElemnets * Sizeof.BYTE);

				// Allocate device output memory

	//			System.out.println("after Memcpy!");
				
				// Set up the kernel parameters: A pointer to an array
				// of pointers which point to the actual values.
				Pointer kernelParameters = Pointer.to(Pointer.to(deviceInputA),
							Pointer.to(deviceOutput),Pointer.to(new int[] { width }),Pointer.to(new int[] { width }),Pointer.to(new int[] { 11 }),Pointer.to(new int[] { 11 }));
				// Call the kernel function.
	//			blockSizeX = (width+block_size-1)/block_size; //the number of thread
				// int gridSizeX = (int)Math.ceil((double)numElements / blockSizeX);
				gridSizeX = (width+block_size-1)/block_size;	 //the number of block
			
				cuLaunchKernel(function, gridSizeX, gridSizeX, 1, // Grid dimension //number of block
						block_size, block_size, 1, // Block dimension //number of thread
						0, null, // Shared memory size and stream
						kernelParameters, null // Kernel- and extra parameters
				);
				cuCtxSynchronize();
				// Allocate host output memory and copy the device output
				// to the host.
				cuMemcpyDtoH(Pointer.to(hostOutput), deviceOutput, numElemnets * Sizeof.BYTE);
				
				
				cuMemFree(deviceInputA);
//				cuMemFree(deviceInputB);
				cuMemFree(deviceOutput);
				
				cuCtxDestroy(context);
				
//				System.out.println("result hostOutput[0] = "+hostOutput[0]);
//				System.out.println("cuda has finished!");
				
								
	}
	
	public void cudaCleanUp(){
		// Clean up.
		
	
		
		
	}
	
	
}