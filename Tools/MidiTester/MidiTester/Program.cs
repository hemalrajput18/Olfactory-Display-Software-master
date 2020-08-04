using System;
using System.Threading;
using Melanchall.DryWetMidi.Common;
using Melanchall.DryWetMidi.Core;
using Melanchall.DryWetMidi.Devices;

namespace MidiTester
{
    class Program
    {

        private static void OnEventSent(object sender, MidiEventSentEventArgs e)
        {
            var midiDevice = (MidiDevice)sender;
            Console.WriteLine($"Event sent to '{midiDevice.Name}' at {DateTime.Now}: {e.Event}");
        }

        static void Main(string[] args)
        {
            Console.WriteLine("Number of MiDi Output devices connected: " + OutputDevice.GetDevicesCount());
            Console.WriteLine("Number of MiDi Input devices connected: " + InputDevice.GetDevicesCount());

            foreach (var device in OutputDevice.GetAll()) {
                Console.WriteLine("[OUT] device ID: " + device.Id + "\ndevice name: " + device.Name);
            }

            foreach (var device in InputDevice.GetAll())
            {
                Console.WriteLine("[IN] device ID: " + device.Id + "\ndevice name: " + device.Name);
            }

            try
            {
                using (var outputDevice = OutputDevice.GetByName("USB MIDI Interface"))
                {
                    outputDevice.EventSent += OnEventSent;
                    Console.WriteLine("Activating pump");
                    outputDevice.SendEvent(new NoteOnEvent((SevenBitNumber)62, (SevenBitNumber)127));
                    Console.WriteLine("Activating fan");
                    outputDevice.SendEvent(new NoteOnEvent((SevenBitNumber)60, (SevenBitNumber)127));
                    Thread.Sleep(5000);
                    Console.WriteLine("Deactivating pump");
                    outputDevice.SendEvent(new NoteOnEvent((SevenBitNumber)62, (SevenBitNumber)0));
                    Console.WriteLine("Deactivating fan");
                    outputDevice.SendEvent(new NoteOnEvent((SevenBitNumber)60, (SevenBitNumber)0));
                }
            }
            catch (Exception e) 
            {
                Console.WriteLine("Midi device not found");
            }
           

            Console.WriteLine("Press any key to exit");
            Console.ReadLine();
        }
    }
}
