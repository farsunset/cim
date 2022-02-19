using CIM_Standard;
using Google.Protobuf;
using System;
using System.Net.Sockets;
using static CIM_Standard.SocketHelper;

namespace CIM_SDK_Tests
{
     class Program
    {
        static void Main(string[] args)
        {
            Console.WriteLine("-------------应用启动中-------------");
             CIMConnect("127.0.0.1",23456, ConsoleMessage).Wait();
        }

        static void ConsoleMessage(MessageModel model) 
        {
            Console.WriteLine(model.ToString());
        }

    }
}
