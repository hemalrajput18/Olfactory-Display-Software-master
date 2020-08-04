using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

namespace Assets.Scripts
{
    public interface CommunicationLayer
    {

        void Init();

        void Close();

        bool Send(params object[] message);

        object Receive();
    }
}
