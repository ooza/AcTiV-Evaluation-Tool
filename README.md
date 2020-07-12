# `AcTiVD-Eval` an evaluation tool for text detection in videos and scene images

## Documentation


`AcTiVD-Eval` is an open-source toolkit written in Java for the evaluation of text regions detection in images. Based on the Wolf algorithm [[1]](#1), it provides a user-friendly interface that facilitates the performance evaluation of text detection methods. The tool takes as input the path of the ground-truth xml file, the output xml file and the video/image (optional):

<p align="center"> 
<img src="/doc/AcTiVDEval_welcome.png">
</p>

The evaluation of a text detection algorithm is generally based on two sets of information: a list G of ground-truth rectangles and a list D of detected ones. During the evaluation process, G and D are compared using a matching function. Final performance values are
then calculated based on the well-known precision, recall and F-score metrics.

Actually, such metrics are computed by measuring the overlap between the intersection area of two rectangles (Gi , Di) and the area of Gi (recall score) or Di (precision score). If an algorithm detects too little text, its recall rate will decrease. Whereas, if it detects too many text regions, its precision rate will decline.
Indeed, in text detection the split and merge cases are very frequent; i.e., one G rectangle may correspond to more than one D rectangle, and vice-versa. In order to correctly match such sets of rectangles, several optimized algorithms have been proposed in the literature. In our work, we use the matching strategy proposed in [[1]](#1) [[2]](#2). Three different matching cases are considered: One-to-one matching, one-to-many matching (split case) and many-to-one matching (merge case).

The following figure depicts the user interface of AcTiVD-Eval as well as a split case, where the ground-truth object is represented by a dashed rectangle and the detection results are in plain line rectangles:

<p align="center"> 
<img width="980" height="680" src="/doc/AcTiVDEval_UI.png">
</p>

Th user can apply the evaluation procedure to the current frame (by clicking on the "Evaluate CF" button) or all video frames (by clicking on the "Evaluate All" button). The "Performance Value" button displays precision, recall and F-measure values:

<p align="center"> 
<img width="980" height="680" src="/doc/AcTiVDEval_outputs.png">
</p>

In the precision and recall curves (illustrated above) x-axis denotes tr values and y-axis denotes tp values (precision and recall values by varying tr and tp from 0 to 1 by a step of 0.1). This helps choosing a good threshold value to decide whether a rectangle is correctly detected or not. As in the RRC ICDAR competitions, the recall and precision thresholds (tr and tp) are set to **0.8 and 0.4**, respectively.

## Installation

This is a desktop application implemented in Java, so all you need to do is build the project using an IDE like NetBeans. The IDE automatically copies all of the JAR files on the projects classpath to your projects dist/lib folder. To run the project go to the dist folder and double-click on the jar file.

```bash
java -jar " Activ-D_Evaluation.jar"
```

## Acknowledgements

We thank Dr. Christian Wolf for making its work publicly available and Mr. Soulayman Chouri for its contribution in the development of this toolkit.

## Citation

If you use `AcTiVD-Eval` please use the following citation

```bibtex
@inproceedings{zayene2016data,
  title={Data, protocol and algorithms for performance evaluation of text detection in Arabic news video},
  author={Zayene, Oussama and Touj, Sameh Masmoudi and Hennebert, Jean and Ingold, Rolf and Amara, Najoua Essoukri Ben},
  booktitle={Advanced Technologies for Signal and Image Processing (ATSIP), 2016 2nd International Conference on},
  pages={258--263},
  year={2016},
  organization={IEEE}
}
```

## Reference

<a id="1">[1]</a> 
C. Wolf and J-M. Jolion. Object count/area graphs for the evaluation of object detection and segmentation algorithms,
 IJDAR, 8(4):280–296, 2006.

<a id="2">[2]</a> 
J. Liang and R. Haralick. Performance evaluation of document layout analysis algorithms on the uw data set,
Document Recognition, volume 3027, pages 149–161, 1997.
