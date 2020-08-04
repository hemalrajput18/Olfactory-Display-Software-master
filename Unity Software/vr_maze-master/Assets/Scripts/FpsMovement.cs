/*
 * written by Joseph Hocking 2017
 * released under MIT license
 * text of license https://opensource.org/licenses/MIT
 */

using Assets.Scripts.DAO;
using System;
using System.IO;
using UnityEngine;

[RequireComponent(typeof(CharacterController))]
[RequireComponent(typeof(StimuliGenerator))]
// basic WASD-style movement control
public class FpsMovement : MonoBehaviour
{

    #region public members
    public float speed = 6.0f;
    public float gravity = -9.8f;

    public float sensitivityHor = 100.0f;
    public float sensitivityVert = 9.0f;

    public float minimumVert = -45.0f;
    public float maximumVert = 45.0f;
    #endregion

    #region private members
    [SerializeField] private OVRCameraRig headCam;
    private float rotationVert = 0;
    private CharacterController charController;
    private StimuliGenerator stimuliGenerator;
    private System.Diagnostics.Stopwatch watch = new System.Diagnostics.Stopwatch();
    #endregion

    #region methods
    void Start()
    {
        charController = GetComponent<CharacterController>();
        watch.Start();
    }

    void Update()
    {
        MoveCharacter();
        RotateCharacter();
        RotateCamera();
    }

    /// <summary>
    /// detects when player exits stimuli zone
    /// </summary>
    /// <param name="other">stimuli zone collider</param>
    private void OnTriggerExit(Collider other)
    {
        MazeDataParser.MazeStimuli m;
        StimuliGenerator.target_areas.TryGetValue(other.name, out m);
        Debug.Log("Stimulus triggered: " + m.STName);
        Debug.Log("Points to resource path: " + m.ResourcePath);
        switch (m.type)
        {
            case "VST":
                Debug.Log("Visual Stimulus Detected");
                try
                {
                    Debug.Log("VST;OFF");
                   // m.ImagePanel.GetComponent<Renderer>().enabled = false;
                }
                catch (Exception e)
                {
                    Debug.Log(e.Message);
                }
                break;
            case "AST":
                Debug.Log("Audio Stimulus Detected");
                try
                {
                    Debug.Log("AST;OFF");
                    AudioSource audioSource = other.gameObject.GetComponent<AudioSource>();
                    audioSource.Stop();
                }
                catch (Exception e)
                {
                    Debug.Log(e.Message);
                }
                break;
            case "OST":
                Debug.Log("Odour Stimulus Detected");
                try
                {
                    Debug.Log("OST;OFF");
                    if (Controller.communicationLayer is MIDI_IO)
                    {
                        Controller.communicationLayer.Send(false, 60, 0);
                        Controller.communicationLayer.Send(false, 62, 0);
                    }
                    else
                    {
                        Controller.communicationLayer.Send("OST_OFF");
                        Controller.communicationLayer.Send("VALVE_CLOSE");
                    }
                    
                }
                catch (Exception e)
                {
                    Debug.Log(e.Message);
                }
                break;
            default:
                Debug.Log("Stimuli Type Parse Error");
                break;
        }
    }

    /// <summary>
    /// detects when the player enters a stimuli zone
    /// </summary>
    /// <param name="other">stimuli zone collider</param>
    private void OnTriggerEnter(Collider other)
    {
        MazeDataParser.MazeStimuli m;
        StimuliGenerator.target_areas.TryGetValue(other.name, out m);
        Debug.Log("Stimulus triggered: " + m.STName);
        Debug.Log("Points to resource path: " + m.ResourcePath);
        switch (m.type)
        {
            case "VST":
                Debug.Log("Visual Stimulus Detected");
                try
                {
                    Debug.Log("VST;" + m.STName + ";" + m.MetaData);
                    m.ImagePanel.GetComponent<Renderer>().enabled = true;
                }
                catch (Exception e)
                {
                    Debug.Log(e.Message);
                }
                break;
            case "AST":
                Debug.Log("Audio Stimulus Detected");
                try
                {
                    Debug.Log("AST;" + m.STName + ";" + m.MetaData);
                    AudioSource audioSource = other.gameObject.GetComponent<AudioSource>();
                    audioSource.Play();
                }
                catch (Exception e)
                {
                    Debug.Log(e.Message);
                }
                break;
            case "OST":
                Debug.Log("Odour Stimulus Detected");
                try
                {
                    if (Controller.communicationLayer is MIDI_IO)
                    {
                        Debug.Log("OST;" + m.STName + ";" + m.MetaData);
                        Controller.communicationLayer.Send(true, 60, 127);
                        Controller.communicationLayer.Send(true, 62, 127);
                    }
                    else
                    {
                        //Controller.communicationLayer.Send("OST;" + m.STName + ";" + m.MetaData);
                        Controller.communicationLayer.Send("OST");
                        Controller.communicationLayer.Send("VALVE_OPEN");
                    }
                    
                }
                catch (Exception e)
                {
                    Debug.Log(e.Message);
                }
                break;
            default:
                Debug.Log("Stimuli Type Parse Error");
                break;
        }
    }

    /// <summary>
    /// moves character
    /// </summary>
    private void MoveCharacter()
    {
        float deltaX = Input.GetAxis("Horizontal") * speed;
        float deltaZ = Input.GetAxis("Vertical") * speed;

        Vector3 movement = new Vector3(deltaX, 0, deltaZ);
        movement = Vector3.ClampMagnitude(movement, speed);

        movement.y = gravity;
        movement *= Time.deltaTime;
        movement = transform.TransformDirection(movement);
        charController.Move(movement);
       
        Vector3 position = charController.transform.localPosition;
        Controller.writer.WriteLine(watch.ElapsedMilliseconds + "," + position.x + "," + position.z);
        Controller.writer.Flush();
        
    }

    /// <summary>
    /// rotates character
    /// </summary>
    private void RotateCharacter()
    {
        float i = Input.GetAxis("Mouse X")*25f;
        //Debug.Log(i);
        if (i > 0.1 || i < -0.1) {
            transform.Rotate(0, i, 0);
        }
    }

    /// <summary>
    /// rotates camera
    /// </summary>
    private void RotateCamera()
    {
        rotationVert -= Input.GetAxis("Mouse Y") * sensitivityVert;
        rotationVert = Mathf.Clamp(rotationVert, minimumVert, maximumVert);
    }
    #endregion

}
