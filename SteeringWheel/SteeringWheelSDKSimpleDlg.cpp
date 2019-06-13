
// DemoWheelSimpleDlg.cpp : implementation file
//

#include "stdafx.h"
#define _WINSOCK_DEPCRECATED 
#include "WS2tcpip.h"
#include "stdafx.h"
#include "SteeringWheelSDKSimple.h"
#include "SteeringWheelSDKSimpleDlg.h"
#include "afxdialogex.h"
#include <chrono>
#include <thread>
#include <iostream>
#pragma comment (lib, "ws2_32.lib");
#pragma warning(disable:4996) ;


#ifdef _DEBUG
#define new DEBUG_NEW
#endif

CSteeringWheelSimpleDlg::CSteeringWheelSimpleDlg(CWnd* pParent /*=NULL*/)
	: CDialogEx(CSteeringWheelSimpleDlg::IDD, pParent)
{
	m_hIcon = AfxGetApp()->LoadIcon(IDR_MAINFRAME);
}

void CSteeringWheelSimpleDlg::DoDataExchange(CDataExchange* pDX)
{
	CDialogEx::DoDataExchange(pDX);
}

BEGIN_MESSAGE_MAP(CSteeringWheelSimpleDlg, CDialogEx)
	ON_WM_SYSCOMMAND()
	ON_WM_PAINT()
	ON_WM_QUERYDRAGICON()
	ON_WM_HSCROLL()
	ON_WM_TIMER()
	ON_BN_CLICKED(IDC_EXIT, &CSteeringWheelSimpleDlg::OnBnClickedExit)
	ON_BN_CLICKED(IDC_LogiSteeringInitialize, &CSteeringWheelSimpleDlg::OnBnClickedLogiSteeringInitialize)
	ON_BN_CLICKED(IDC_LogiSteeringInitializeWithWindow, &CSteeringWheelSimpleDlg::OnBnClickedLogiSteeringInitializeWithWindow)

END_MESSAGE_MAP()


// CSteeringWheelSimpleDlg message handlers

BOOL CSteeringWheelSimpleDlg::OnInitDialog()
{
	CDialogEx::OnInitDialog();

	// Set the icon for this dialog.  The framework does this automatically
	//  when the application's main window is not a dialog
	SetIcon(m_hIcon, TRUE);			// Set big icon
	SetIcon(m_hIcon, FALSE);		// Set small icon

	m_deviceIndex = 0;
	m_offsetPercentage = 90;
	m_saturationPercentage = 90;
	m_coefficientPercentage = 90;
	m_magnitudePercentage = 90;
	m_usableRangePercentage = 90;

	m_allDevices = false;
	m_spring = false;
	m_constant = false;
	m_damper = false;
	m_dirtRoad = false;
	m_bumpyRoad = false;
	m_slipperyRoad = false;
	m_carAirborne = false;
	m_softstop = false;

	SetDlgItemInt(IDC_DEVICE, m_deviceIndex, 0);

	SetTimer(1, 1000 / 30, NULL);

	return TRUE;  // return TRUE  unless you set the focus to a control
}

// If you add a minimize button to your dialog, you will need the code below
//  to draw the icon.  For MFC applications using the document/view model,
//  this is automatically done for you by the framework.

void CSteeringWheelSimpleDlg::OnPaint()
{
	if (IsIconic())
	{
		CPaintDC dc(this); // device context for painting

		SendMessage(WM_ICONERASEBKGND, reinterpret_cast<WPARAM>(dc.GetSafeHdc()), 0);

		// Center icon in client rectangle
		int cxIcon = GetSystemMetrics(SM_CXICON);
		int cyIcon = GetSystemMetrics(SM_CYICON);
		CRect rect;
		GetClientRect(&rect);
		int x = (rect.Width() - cxIcon + 1) / 2;
		int y = (rect.Height() - cyIcon + 1) / 2;

		// Draw the icon
		dc.DrawIcon(x, y, m_hIcon);
	}
	else
	{
		CDialogEx::OnPaint();
	}
}

void CSteeringWheelSimpleDlg::OnTimer(UINT_PTR nIDEvent)
{
	UNREFERENCED_PARAMETER(nIDEvent);

	// Update the input device every timer message.

	//if the return value is false, means that the application has not been initialized yet and there is no hwnd available
	if (!LogiUpdate()) return;

	if (m_spring)
	{
		if (m_allDevices)
		{
			for (int t_index = 0; LogiIsConnected(t_index); t_index++)
			{
				LogiPlaySpringForce(t_index, m_offsetPercentage, m_saturationPercentage, m_coefficientPercentage);
			}
		}
		else
		{
			LogiPlaySpringForce(m_deviceIndex, m_offsetPercentage, m_saturationPercentage, m_coefficientPercentage);
		}
	}
	if (m_constant)
	{
		if (m_allDevices)
		{
			for (int t_index = 0; LogiIsConnected(t_index); t_index++)
			{
				LogiPlayConstantForce(t_index, m_magnitudePercentage);
			}
		}
		else
		{
			LogiPlayConstantForce(m_deviceIndex, m_magnitudePercentage);
		}
	}
	if (m_damper)
	{
		if (m_allDevices)
		{
			for (int t_index = 0; LogiIsConnected(t_index); t_index++)
			{
				LogiPlayDamperForce(t_index, m_coefficientPercentage);
			}
		}
		else
		{
			LogiPlayDamperForce(m_deviceIndex, m_coefficientPercentage);
		}
	}
	if (m_dirtRoad)
	{
		if (m_allDevices)
		{
			for (int t_index = 0; LogiIsConnected(t_index); t_index++)
			{
				LogiPlayDirtRoadEffect(t_index, m_magnitudePercentage);
			}
		}
		else
		{
			LogiPlayDirtRoadEffect(m_deviceIndex, m_magnitudePercentage);
		}
	}
	if (m_bumpyRoad)
	{
		if (m_allDevices)
		{
			for (int t_index = 0; LogiIsConnected(t_index); t_index++)
			{
				LogiPlayBumpyRoadEffect(t_index, m_magnitudePercentage);
			}
		}
		else
		{
			LogiPlayBumpyRoadEffect(m_deviceIndex, m_magnitudePercentage);
		}
	}
	if (m_slipperyRoad)
	{
		if (m_allDevices)
		{
			for (int t_index = 0; LogiIsConnected(t_index); t_index++)
			{
				LogiPlaySlipperyRoadEffect(t_index, m_magnitudePercentage);
			}
		}
		else
		{
			LogiPlaySlipperyRoadEffect(m_deviceIndex, m_magnitudePercentage);
		}
	}
	if (m_carAirborne)
	{
		if (m_allDevices)
		{
			for (int t_index = 0; LogiIsConnected(t_index); t_index++)
			{
				LogiPlayCarAirborne(t_index);
			}
		}
		else
		{
			LogiPlayCarAirborne(m_deviceIndex);
		}
	}
	if (m_softstop)
	{
		if (m_allDevices)
		{
			for (int t_index = 0; LogiIsConnected(t_index); t_index++)
			{
				LogiPlaySoftstopForce(t_index, m_usableRangePercentage);
			}
		}
		else
		{
			LogiPlaySoftstopForce(m_deviceIndex, m_usableRangePercentage);
		}
	}
}

void CSteeringWheelSimpleDlg::OnHScroll(UINT nSBCode, UINT nPos, CScrollBar* pScrollBar)
{
	int CurPos = pScrollBar->GetScrollPos();

	// Determine the new position of scroll box.
	switch (nSBCode)
	{
	case SB_LEFT:      // Scroll to far left.
		CurPos = SCROLL_MIN;
		break;

	case SB_RIGHT:      // Scroll to far right.
		CurPos = SCROLL_MAX;
		break;

	case SB_ENDSCROLL:   // End scroll.
		break;

	case SB_LINELEFT:      // Scroll left.
		if (CurPos > SCROLL_MIN)
			CurPos--;
		break;

	case SB_LINERIGHT:   // Scroll right.
		if (CurPos < SCROLL_MAX)
			CurPos++;
		break;

	case SB_PAGELEFT:    // Scroll one page left.
	{
		// Get the page size. 
		SCROLLINFO   info;
		pScrollBar->GetScrollInfo(&info, SIF_ALL);

		if (CurPos > SCROLL_MIN)
			CurPos = max(0, CurPos - (int)info.nPage);
	}
	break;

	case SB_PAGERIGHT:      // Scroll one page right
	{
		// Get the page size. 
		SCROLLINFO   info;
		pScrollBar->GetScrollInfo(&info, SIF_ALL);

		if (CurPos < SCROLL_MAX)
			CurPos = min(122, CurPos + (int)info.nPage);
	}
	break;

	case SB_THUMBPOSITION: // Scroll to absolute position. nPos is the position
		CurPos = nPos;      // of the scroll box at the end of the drag operation.
		break;

	case SB_THUMBTRACK:   // Drag scroll box to specified position. nPos is the
		CurPos = nPos;     // position that the scroll box has been dragged to.
		break;
	}

	pScrollBar->SetScrollPos(CurPos);

	m_offsetPercentage = 70;
	m_saturationPercentage = 70;
	m_coefficientPercentage = 70;
	m_magnitudePercentage = 70;
	m_usableRangePercentage = 70;

	if (m_fullRange)
	{
		m_offsetPercentage = (m_offsetPercentage * 2) - 100;
		m_coefficientPercentage = (m_coefficientPercentage * 2) - 100;
		m_magnitudePercentage = (m_magnitudePercentage * 2) - 100;
	}

	CDialogEx::OnHScroll(nSBCode, nPos, pScrollBar);
}

// The system calls this function to obtain the cursor to display while the user drags
//  the minimized window.
HCURSOR CSteeringWheelSimpleDlg::OnQueryDragIcon()
{
	return static_cast<HCURSOR>(m_hIcon);
}

void CSteeringWheelSimpleDlg::OnBnClickedExit()
{
	CDialog::OnOK();
}

void CSteeringWheelSimpleDlg::OnBnClickedLogiSteeringInitialize()
{
	if (LogiSteeringInitialize(true))
	{
		::SetWindowText(::GetDlgItem(m_hWnd, IDC_RESULT), L"LogiSteeringInitialize returned TRUE");
	}
	else
	{
		::SetWindowText(::GetDlgItem(m_hWnd, IDC_RESULT), L"LogiSteeringInitialize returned FALSE");
	}

	CSteeringWheelSimpleDlg::OnBnClickedLogiPlayBumpyRoadEffect();
	CSteeringWheelSimpleDlg::OnBnClickedLogiPlayBumpyRoadEffect();
	CSteeringWheelSimpleDlg::OnBnClickedLogiStopBumpyRoadEffect();

}

void CSteeringWheelSimpleDlg::OnBnClickedLogiPlayBumpyRoadEffect()
{
	if (m_allDevices)
	{
		for (int t_index = 0; LogiIsConnected(t_index); t_index++)
		{
			LogiPlayBumpyRoadEffect(t_index, m_magnitudePercentage);
		}
		::SetWindowText(::GetDlgItem(m_hWnd, IDC_RESULT), L"LogiPlayBumpyRoadEffect is now active on all devices");
	}
	else
	{
		if (LogiPlayBumpyRoadEffect(m_deviceIndex, m_magnitudePercentage))
		{
			::SetWindowText(::GetDlgItem(m_hWnd, IDC_RESULT), L"LogiPlayBumpyRoadEffect returned TRUE");
		}
		else
		{
			::SetWindowText(::GetDlgItem(m_hWnd, IDC_RESULT), L"LogiPlayBumpyRoadEffect returned FALSE");
		}
	}
}

void CSteeringWheelSimpleDlg::OnBnClickedLogiStopBumpyRoadEffect()
{
	if (m_allDevices)
	{
		for (int t_index = 0; LogiIsConnected(t_index); t_index++)
		{
			LogiStopBumpyRoadEffect(t_index);
		}
		::SetWindowText(::GetDlgItem(m_hWnd, IDC_RESULT), L"LogiStopBumpyRoadEffect is now active on all devices");
	}
	else
	{
		if (LogiStopBumpyRoadEffect(m_deviceIndex))
		{
			::SetWindowText(::GetDlgItem(m_hWnd, IDC_RESULT), L"LogiStopBumpyRoadEffect returned TRUE");
		}
		else
		{
			::SetWindowText(::GetDlgItem(m_hWnd, IDC_RESULT), L"LogiStopBumpyRoadEffect returned FALSE");
		}
	}
}

void CSteeringWheelSimpleDlg::OnEnChangeDevice()
{
	m_deviceIndex = GetDlgItemInt(IDC_DEVICE, 0, 0);
}

//FUNZIONE DEL SERVER DA IMPLEMENTARE
void CSteeringWheelSimpleDlg::OnBnClickedLogiSteeringInitializeWithWindow()
{
	//inizializzazione
	::SetWindowText(::GetDlgItem(m_hWnd, IDC_RESULT), L"Server initialized");

	//Initialize winsock
	WSADATA wsData;
	WORD ver MAKEWORD(2, 2);
	int wsOk = WSAStartup(ver, &wsData);
	if (wsOk != 0) {
		::SetWindowText(::GetDlgItem(m_hWnd, IDC_RESULT), L"Can't Initialize winsock! Quitting");
		return;
	}

	//Create a socket
	int listening = socket(AF_INET, SOCK_STREAM, 0);
	if (listening < 0) {
		::SetWindowText(::GetDlgItem(m_hWnd, IDC_RESULT), L"Can't create a socket! Quitting");
		return;
	}

	sockaddr_in hint;
	hint.sin_family = AF_INET;
	hint.sin_port = htons(4000);
	inet_pton(AF_INET, "192.168.0.6", &hint.sin_addr.s_addr);

	//Wait for connection

	::SetWindowText(::GetDlgItem(m_hWnd, IDC_RESULT), L"Waiting ");
	sockaddr_in client;
	int clientSize = sizeof(client); //in order to get some client information and then display them

	connect(listening, (const struct sockaddr*) &hint, sizeof(hint));
	char host[NI_MAXHOST]; // Client's remote name
	char service[NI_MAXHOST]; //Service (i.e. port) the client is connect on
	ZeroMemory(host, NI_MAXHOST);
	ZeroMemory(service, NI_MAXHOST);

	//Close listening socket
	char buf[4096];
	int flag = 0;

	::SetWindowText(::GetDlgItem(m_hWnd, IDC_RESULT), L"Connected!");
	
	while (flag == 0) {
		ZeroMemory(buf, 4096);

		//Wait for client to send data
		int bytesReceived = recv(listening, buf, 4096, 0);

		if (bytesReceived == SOCKET_ERROR) {
			::SetWindowText(::GetDlgItem(m_hWnd, IDC_RESULT), L"Error in recv()! Quitting");
			break;
		}

		if (bytesReceived == 0) {
			break;
		}

		send(listening, buf, bytesReceived + 1, 0);

		//SE RICEVE "i" DEVE INIZIARE A VIBRARE

		if (buf[0] == 'i')
		{
			CSteeringWheelSimpleDlg::OnBnClickedLogiPlayBumpyRoadEffect();

		}

		//SE RICEVE "f" DEVE SMETTERE DI VIBRARE
		else if (buf[0] == 'f')
		{
			CSteeringWheelSimpleDlg::OnBnClickedLogiStopBumpyRoadEffect();

		}

		//SE RICEVE "e" DEVE CHIUDERE LA COMUNICAZIONE
		else if (buf[0] == 'e')
		{
			CSteeringWheelSimpleDlg::OnBnClickedLogiStopBumpyRoadEffect();

			//Cleanup winsock

			WSACleanup();

			//per uscire dal ciclo e dalla funzione
			flag = 1;

		}

	}

}