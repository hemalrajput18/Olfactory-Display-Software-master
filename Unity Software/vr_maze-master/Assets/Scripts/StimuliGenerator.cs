using System;
using System.Collections.Generic;
using UnityEditor;
using UnityEngine;
using UnityEngine.AddressableAssets;
using UnityEngine.EventSystems;

public class StimuliGenerator : MonoBehaviour
{

    #region public members
    public MazeDataParser parser;
    public Material vstMaterial;

    public static Dictionary<String, MazeDataParser.MazeStimuli> target_areas =
        new Dictionary<String, MazeDataParser.MazeStimuli>();
    #endregion

    #region private members
    private static int stimuliCounter = 0;
    #endregion

    #region methods
    /// <summary>
    /// Reads through the list of stimuli objects parsed from the config file. Then creates stimuli objects in the maze.
    /// </summary>
    /// <param name="stimuli">Stimuli object</param>
    /// <returns></returns>
    public GameObject AddStimuli(MazeDataParser.MazeStimuli stimuli)
    {
        var stimulusGO = new GameObject();
        stimulusGO.transform.position = Vector3.zero;
        stimulusGO.name = "Stimuli";
        stimulusGO.tag = "Generated";
        stimulusGO.layer = LayerMask.NameToLayer("Stimuli");
        var stimulusMesh = new Mesh();

        // Generate test stimuli mesh and setup GameObject and necessary components.
        var newVertices = new List<Vector3>();
        var newUVs = new List<Vector2>();

        stimulusMesh.subMeshCount = 1;
        var triangles = new List<int>();

        var offset = new Vector3((float)(stimuli.cell.x - 1), 0, (float)(stimuli.cell.y - 1));
        var scale = new Vector3(parser.cellWidth, parser.cellHeight, parser.cellWidth);
        var verts = new List<Vector3>();

        float w2 = (float)parser.cellWidth / 2;
        float x = w2 * (2 * stimuli.cell.x - 1);
        float y = (float)parser.cellHeight / 2;
        float z = w2 * (2 * stimuli.cell.y - 1);

        Vector3 position = new Vector3(x + (float)stimuli.x_offset, y, z + (float)stimuli.z_offset);
        Vector3 size = new Vector3((float)(parser.cellWidth - 1), (float)(parser.cellHeight - 1), (float)(parser.cellWidth - 1));

        GameObject a = createStimuliArea(position, size, stimuli);
        target_areas.Add(a.name, stimuli);
        return stimulusGO;
    }

    /// <summary>
    /// Generates the stimulus area by reading in the position of the stimulus and its size.
    /// </summary>
    /// <param name="position">Stimulus position</param>
    /// <param name="size">Stimulus size</param>
    /// <returns>GameObject cube with collider</returns>
    public GameObject createStimuliArea(Vector3 position, Vector3 size, MazeDataParser.MazeStimuli stimuli)
    {
        GameObject area = GameObject.CreatePrimitive(PrimitiveType.Cube);
        area.name = Convert.ToString(stimuliCounter++);
        area.transform.position = position;
        area.transform.localScale += size;
        area.transform.localScale = new Vector3(area.transform.localScale.x * stimuli.size.x, 
                                                area.transform.localScale.y * stimuli.size.y,
                                                area.transform.localScale.z * stimuli.size.z);
        area.GetComponent<Renderer>().material.color = new Color(1.0f, 1.0f, 1.0f, 1.0f);
        area.AddComponent<Light>().type = LightType.Point;
        area.GetComponent<Collider>().isTrigger = true;
        Collider c = area.GetComponent<Collider>();
        c.isTrigger = true;

        area.GetComponent<Renderer>().enabled = false;

        switch (stimuli.type)
        {
            case "AST":
                AudioSource audioSource = area.AddComponent<AudioSource>();
                audioSource.clip = Resources.Load<AudioClip>("Audio/"+stimuli.ResourcePath);
                //audioSource.clip = Resources.Load<AudioClip>("Audio/bensound-summer");
                if (audioSource == null)
                {
                    Debug.Log("Audio source not found");
                }
                else 
                {
                    Debug.Log("Audio resource loaded");
                }
                
                break;
            case "VST":
                GameObject imagePanel = GameObject.CreatePrimitive(PrimitiveType.Plane);
                imagePanel.name = "img";

                Vector3 panelPosition = new Vector3();
                Vector3 panelScale = new Vector3();
                float offset = 0.2f;

                switch (stimuli.MetaData)
                {
                    case "FRONT":
                        panelPosition = position + new Vector3(0, 0, -area.transform.localScale.z / 2 + (offset + 2 * parser.wallFraction));
                        panelScale = new Vector3(imagePanel.transform.localScale.x / 2.5f, imagePanel.transform.localScale.y / 2.5f,
                            imagePanel.transform.localScale.z / 2.5f);
                        imagePanel.transform.Rotate(90f, 0f, 0f);
                        break;
                    case "RIGHT":
                        panelPosition = position + new Vector3(area.transform.localScale.x / 2 - (offset + 2 * parser.wallFraction), 0, 0);
                        panelScale = new Vector3(imagePanel.transform.localScale.x / 2.5f, imagePanel.transform.localScale.y / 2.5f,
                            imagePanel.transform.localScale.z / 2.5f);
                        imagePanel.transform.Rotate(90f, 0f, 90f);
                        break;
                    case "BACK":
                        panelPosition = position + new Vector3(0, 0, area.transform.localScale.z / 2 - (offset + 2 * parser.wallFraction));
                        panelScale = new Vector3(imagePanel.transform.localScale.x / 2.5f, imagePanel.transform.localScale.y / 2.5f,
                            imagePanel.transform.localScale.z / 2.5f);
                        imagePanel.transform.Rotate(90f, -90f, 90f);
                        break;
                    case "LEFT":
                        panelPosition = position + new Vector3(-area.transform.localScale.x / 2 + (offset + 2 * parser.wallFraction), 0, 0);
                        panelScale = new Vector3(imagePanel.transform.localScale.x / 2.5f, imagePanel.transform.localScale.y / 2.5f,
                            imagePanel.transform.localScale.z / 2.5f);
                        imagePanel.transform.Rotate(-90f, 0f, -90f);
                        break;
                    case "CEILING":
                        panelPosition = position + new Vector3(0f, area.transform.localScale.y / 2 - offset, 0f);
                        panelScale = new Vector3(imagePanel.transform.localScale.x / 2.5f, imagePanel.transform.localScale.y / 2.5f,
                            imagePanel.transform.localScale.z / 2.5f);
                        imagePanel.transform.Rotate(0f, 0f, 180f);
                        break;
                    case "FLOOR":
                        panelPosition = position + new Vector3(0f, -area.transform.localScale.y / 2 + offset, 0f);
                        panelScale = new Vector3(imagePanel.transform.localScale.x / 2.5f, imagePanel.transform.localScale.y / 2.5f,
                            imagePanel.transform.localScale.z / 2.5f);
                        imagePanel.transform.Rotate(180f, 0f, -180f);
                        break;
                    default:
                        break;
                }

                imagePanel.transform.localScale = panelScale;
                imagePanel.transform.position = panelPosition;

                imagePanel.AddComponent<Light>().type = LightType.Rectangle;
                imagePanel.GetComponent<Collider>().enabled = false;
                Renderer panelRenderer = imagePanel.GetComponent<Renderer>();

                Texture2D texture = ((Texture2D)Resources.Load("Images/"+stimuli.ResourcePath));
                if (texture == null)
                {
                    Debug.Log("Texture not found");
                }
                else
                {
                    Debug.Log("Texture loaded");
                }

                Material material = new Material(Shader.Find("Diffuse"));
                material.mainTexture = texture;
                panelRenderer.material = material;
                //panelRenderer.material.SetColor("_Color",Color.red);

                panelRenderer.enabled = false; //hides image
                stimuli.ImagePanel = imagePanel;
                break;
        }

        return area;
    }
    #endregion

}
