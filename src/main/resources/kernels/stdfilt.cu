extern "C"


__global__ void stdfilt(int *d_Data, int *d_Result, int dataW, int dataH, int kernel_nWidth, int kernel_nHeight)
{
  	int col = blockDim.x * blockIdx.x + threadIdx.x;
	int row = blockDim.y * blockIdx.y + threadIdx.y;

	int ave = 0, ave_cnt = 0, dev = 0, dev_cnt = 0, tmp_dev = 0;
	int inf = 1 / ave;
	int kernel_mi = 0, kernel_mj = 0;
	int offset_x = 0, offset_y = 0;
	int mj = 0, mi = 0;


	//	int tid;

	//	tid = dataW*row + col;

	if (row > dataH || col > dataW)
		return;


	kernel_mi = kernel_nHeight / 2;
	kernel_mj = kernel_nWidth / 2;



	for (mi = kernel_mi*-1; mi <= kernel_mi; mi += 1) {
		for (mj = kernel_mj*-1; mj <= kernel_mj; mj += 1) {
			offset_y = row + mi;                                 //  Ŀ�κ��� x,y ũ�Ⱑ ������� offset ���� ����
			offset_x = col + mj;


			if (offset_y < 0 || offset_y >= dataH)     //  y�� Ŀ�κ��� �۰ų� ũ�� ����,  - mi�� ������ //  ũ�� -mi�� ���������ν� �̹��� ������ ����� ���� ����
				offset_y = row + (mi*-1);


			if (offset_x < 0 || offset_x >= dataW)
				offset_x = col + (mj*-1);

			//					
			//if (kernel->data[(kernel_mi + mi)*kernel->nWidth + kernel_mj + mj] != 1)      //���ʿ��� if
			//{
			//	printf("this is first if\n");
			//	continue;
			//}
			//if ((offset_y*dst->nWidth) + offset_x >= src->nHeight*src->nWidth)			 //���ʿ��� if
			//{ 
			//	printf("this is second if\n");
			//	continue;
			//}


			ave += d_Data[(offset_y*dataW) + offset_x];       /// ���Ϳ����� ��� ���ؼ� ��հ��� ���� ����
			ave_cnt++;


		}
	}

	ave /= ave_cnt;


	for (mi = kernel_mi*-1; mi <= kernel_mi; mi += 1) {                   /// ���Ϳ����� ��� ���ؼ� ��հ��� ����
		for (mj = kernel_mj*-1; mj <= kernel_mj; mj += 1) {
			offset_y = row + mi;
			offset_x = col + mj;
			if (offset_y < 0 || offset_y >= dataH)
				offset_y = row + (mi*-1);
			if (offset_x < 0 || offset_x >= dataW)
				offset_x = col + (mj*-1);

			//if ((offset_y*dst->nWidth) + offset_x >= src->nHeight*src->nWidth)		 //���ʿ��� if
			//{ 
			//	printf("this is third if");
			//	continue;
			//}

			tmp_dev = (d_Data[(offset_y*dataW) + offset_x] - ave)*(d_Data[(offset_y*dataW) + offset_x] - ave); //������ ����
			if (inf == tmp_dev)
				tmp_dev = 0;

			dev += tmp_dev;
			dev_cnt += 1;
		}
	}


//	tmp_dev = sqrt(dev/ (dev_cnt == kernel_nHeight*kernel_nWidth ? dev_cnt -= 1 : dev_cnt));    //�ݿø�
	if (inf != tmp_dev)
		d_Result[row*dataW + col] = tmp_dev;
	//		src[i*nWidth + j] = ave;
	ave = 0;
	ave_cnt = 0;
	dev = 0;
	dev_cnt = 0;

	//d_Result[tid] = tid;

}