using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using Melanchall.DryWetMidi.Common;
using Melanchall.DryWetMidi.Core;
using Melanchall.DryWetMidi.Devices;
using UnityEngine;

namespace Assets.Scripts.DAO
{
    class MIDI_IO : CommunicationLayer
    {

        private bool isDeviceConnected = false;
        OutputDevice outputDevice;

        private void OnEventSent(object sender, MidiEventSentEventArgs e)
        {
            var midiDevice = (MidiDevice)sender;
            Debug.Log($"Event sent to '{midiDevice.Name}' at {DateTime.Now}: {e.Event}");
        }

        public void Close()
        {
            throw new NotImplementedException();
        }

        public void Init()
        {
            outputDevice = OutputDevice.GetByName("USB MIDI Interface");
            if (outputDevice == null)
            {
                isDeviceConnected = false;
                Debug.Log("No MiDi devices connected");
            }
            else
            {
                isDeviceConnected = true;
                outputDevice.EventSent += OnEventSent;
                foreach (var a in OutputDevice.GetAll())
                {
                    Debug.Log("Device with ID: " + a.Id + " Connected");
                }
            }
        }

        public object Receive()
        {
            throw new NotImplementedException();
        }

        public bool Send(params object[] message)
        {
            if (isDeviceConnected)
            {
                if (message.Length == 3)
                {
                    try
                    {

                        bool isNoteOnEvent = (bool)message[0];
                        int note = (int)message[1];
                        int velocity = (int)message[2];

                        //Note on event
                        if ((bool)message[0] == true)
                        {
                            NoteOnEvent on = new NoteOnEvent((SevenBitNumber)note, (SevenBitNumber)velocity);
                            outputDevice.SendEvent(on);
                        }
                        else
                        {
                            NoteOffEvent off = new NoteOffEvent((SevenBitNumber)note, (SevenBitNumber)velocity);
                            outputDevice.SendEvent(off);
                        }
                    }
                    catch (Exception e) {
                        Debug.Log(e.StackTrace);
                        return false;
                    }
                }
                else
                {
                    return false;
                }
                return true;
            }
            else
            {
                return false;
                throw new Exception("No devices connected");
            }
        }
    }
}
